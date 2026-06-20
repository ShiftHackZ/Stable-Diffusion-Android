#include "bonsai_tokenizer.h"

#include "bonsai_model_config.h"
#include "bonsai_prompt.h"

#include <algorithm>
#include <cctype>
#include <limits>
#include <sstream>
#include <stdexcept>
#include <unordered_set>

namespace {

void skip_whitespace(const std::string& json, size_t* index) {
    while (*index < json.size() &&
           std::isspace(static_cast<unsigned char>(json[*index])) != 0) {
        (*index)++;
    }
}

void require_index(const std::string& json, size_t index, const std::string& label) {
    if (index >= json.size()) {
        throw std::runtime_error("invalid Bonsai tokenizer JSON while reading " + label);
    }
}

uint32_t hex_value(char value) {
    if (value >= '0' && value <= '9') {
        return static_cast<uint32_t>(value - '0');
    }
    if (value >= 'a' && value <= 'f') {
        return static_cast<uint32_t>(value - 'a' + 10);
    }
    if (value >= 'A' && value <= 'F') {
        return static_cast<uint32_t>(value - 'A' + 10);
    }
    throw std::runtime_error("invalid Bonsai tokenizer JSON unicode escape");
}

uint32_t parse_hex4(const std::string& json, size_t index) {
    if (index + 4U > json.size()) {
        throw std::runtime_error("invalid Bonsai tokenizer JSON unicode escape");
    }
    return (hex_value(json[index]) << 12U) |
        (hex_value(json[index + 1U]) << 8U) |
        (hex_value(json[index + 2U]) << 4U) |
        hex_value(json[index + 3U]);
}

std::string utf8_codepoint(uint32_t codepoint) {
    std::string output;
    if (codepoint <= 0x7FU) {
        output.push_back(static_cast<char>(codepoint));
    } else if (codepoint <= 0x7FFU) {
        output.push_back(static_cast<char>(0xC0U | (codepoint >> 6U)));
        output.push_back(static_cast<char>(0x80U | (codepoint & 0x3FU)));
    } else if (codepoint <= 0xFFFFU) {
        output.push_back(static_cast<char>(0xE0U | (codepoint >> 12U)));
        output.push_back(static_cast<char>(0x80U | ((codepoint >> 6U) & 0x3FU)));
        output.push_back(static_cast<char>(0x80U | (codepoint & 0x3FU)));
    } else if (codepoint <= 0x10FFFFU) {
        output.push_back(static_cast<char>(0xF0U | (codepoint >> 18U)));
        output.push_back(static_cast<char>(0x80U | ((codepoint >> 12U) & 0x3FU)));
        output.push_back(static_cast<char>(0x80U | ((codepoint >> 6U) & 0x3FU)));
        output.push_back(static_cast<char>(0x80U | (codepoint & 0x3FU)));
    } else {
        throw std::runtime_error("invalid Bonsai tokenizer unicode codepoint");
    }
    return output;
}

std::string parse_string_at(const std::string& json, size_t* index) {
    require_index(json, *index, "string");
    if (json[*index] != '"') {
        throw std::runtime_error("invalid Bonsai tokenizer JSON string");
    }
    (*index)++;

    std::string output;
    while (*index < json.size()) {
        const char value = json[*index];
        (*index)++;
        if (value == '"') {
            return output;
        }
        if (value != '\\') {
            output.push_back(value);
            continue;
        }

        require_index(json, *index, "escape");
        const char escaped = json[*index];
        (*index)++;
        switch (escaped) {
            case '"':
            case '\\':
            case '/':
                output.push_back(escaped);
                break;
            case 'b':
                output.push_back('\b');
                break;
            case 'f':
                output.push_back('\f');
                break;
            case 'n':
                output.push_back('\n');
                break;
            case 'r':
                output.push_back('\r');
                break;
            case 't':
                output.push_back('\t');
                break;
            case 'u':
                if (*index + 4U > json.size()) {
                    throw std::runtime_error("invalid Bonsai tokenizer JSON unicode escape");
                }
                output += utf8_codepoint(parse_hex4(json, *index));
                *index += 4U;
                break;
            default:
                throw std::runtime_error("invalid Bonsai tokenizer JSON escape");
        }
    }

    throw std::runtime_error("unterminated Bonsai tokenizer JSON string");
}

size_t find_value_start(const std::string& json, const std::string& key) {
    const std::string quoted_key = "\"" + key + "\"";
    const size_t key_index = json.find(quoted_key);
    if (key_index == std::string::npos) {
        return std::string::npos;
    }

    const size_t colon_index = json.find(':', key_index + quoted_key.size());
    if (colon_index == std::string::npos) {
        throw std::runtime_error("invalid Bonsai tokenizer JSON key: " + key);
    }

    size_t value_index = colon_index + 1U;
    skip_whitespace(json, &value_index);
    return value_index;
}

bool optional_string_value(
    const std::string& json,
    const std::string& key,
    std::string* output
) {
    size_t value_index = find_value_start(json, key);
    if (value_index == std::string::npos) {
        return false;
    }
    require_index(json, value_index, key);
    if (json[value_index] != '"') {
        return false;
    }
    *output = parse_string_at(json, &value_index);
    return true;
}

bool optional_bool_value(
    const std::string& json,
    const std::string& key,
    bool* output
) {
    size_t value_index = find_value_start(json, key);
    if (value_index == std::string::npos) {
        return false;
    }
    if (json.compare(value_index, 4U, "true") == 0) {
        *output = true;
        return true;
    }
    if (json.compare(value_index, 5U, "false") == 0) {
        *output = false;
        return true;
    }
    throw std::runtime_error("invalid Bonsai tokenizer boolean JSON key: " + key);
}

void skip_string(const std::string& json, size_t* index) {
    (void) parse_string_at(json, index);
}

void skip_container(const std::string& json, size_t* index, char open, char close) {
    require_index(json, *index, "container");
    if (json[*index] != open) {
        throw std::runtime_error("invalid Bonsai tokenizer JSON container");
    }

    uint64_t depth = 0;
    while (*index < json.size()) {
        const char value = json[*index];
        if (value == '"') {
            skip_string(json, index);
            continue;
        }
        if (value == open) {
            depth++;
        } else if (value == close) {
            if (depth == 0) {
                throw std::runtime_error("invalid Bonsai tokenizer JSON container depth");
            }
            depth--;
            (*index)++;
            if (depth == 0) {
                return;
            }
            continue;
        }
        (*index)++;
    }

    throw std::runtime_error("unterminated Bonsai tokenizer JSON container");
}

size_t container_end(const std::string& json, size_t start, char open, char close) {
    size_t index = start;
    skip_container(json, &index, open, close);
    return index;
}

std::string extract_container_for_key(
    const std::string& json,
    const std::string& key,
    char open,
    char close
) {
    const size_t start = find_value_start(json, key);
    if (start == std::string::npos) {
        throw std::runtime_error("missing " + key + " in Bonsai tokenizer JSON");
    }
    require_index(json, start, key);
    if (json[start] != open) {
        throw std::runtime_error("invalid " + key + " in Bonsai tokenizer JSON");
    }
    const size_t end = container_end(json, start, open, close);
    return json.substr(start, end - start);
}

int64_t parse_integer_value(const std::string& json, size_t* index) {
    skip_whitespace(json, index);
    require_index(json, *index, "integer");
    const size_t start = *index;
    if (json[*index] == '-') {
        (*index)++;
    }
    while (*index < json.size() &&
           std::isdigit(static_cast<unsigned char>(json[*index])) != 0) {
        (*index)++;
    }
    if (start == *index || (json[start] == '-' && start + 1U == *index)) {
        throw std::runtime_error("invalid Bonsai tokenizer integer");
    }
    return std::stoll(json.substr(start, *index - start));
}

bool optional_token_string(
    const std::string& json,
    const std::string& key,
    std::string* output
) {
    size_t value_index = find_value_start(json, key);
    if (value_index == std::string::npos) {
        return false;
    }
    require_index(json, value_index, key);
    if (json[value_index] == '"') {
        *output = parse_string_at(json, &value_index);
        return true;
    }
    if (json[value_index] == '{') {
        const size_t end = container_end(json, value_index, '{', '}');
        const std::string object = json.substr(value_index, end - value_index);
        return optional_string_value(object, "content", output);
    }
    return false;
}

int32_t checked_token_id(int64_t value, const std::string& label) {
    if (value < 0 || value > static_cast<int64_t>(std::numeric_limits<int32_t>::max())) {
        throw std::runtime_error("invalid Bonsai tokenizer " + label + " id");
    }
    return static_cast<int32_t>(value);
}

bool optional_int_value(
    const std::string& json,
    const std::string& key,
    int32_t* output
) {
    size_t value_index = find_value_start(json, key);
    if (value_index == std::string::npos) {
        return false;
    }
    *output = checked_token_id(parse_integer_value(json, &value_index), key);
    return true;
}

std::unordered_map<std::string, int32_t> parse_vocab_entries(const std::string& vocab_json) {
    std::unordered_map<std::string, int32_t> vocab;
    size_t index = 0;
    skip_whitespace(vocab_json, &index);
    require_index(vocab_json, index, "vocab");
    if (vocab_json[index] != '{') {
        throw std::runtime_error("invalid Bonsai tokenizer vocab");
    }
    index++;

    while (index < vocab_json.size()) {
        skip_whitespace(vocab_json, &index);
        if (index < vocab_json.size() && vocab_json[index] == '}') {
            return vocab;
        }

        const std::string key = parse_string_at(vocab_json, &index);
        skip_whitespace(vocab_json, &index);
        require_index(vocab_json, index, "vocab colon");
        if (vocab_json[index] != ':') {
            throw std::runtime_error("invalid Bonsai tokenizer vocab member");
        }
        index++;
        vocab.emplace(key, checked_token_id(parse_integer_value(vocab_json, &index), "vocab"));

        skip_whitespace(vocab_json, &index);
        if (index < vocab_json.size() && vocab_json[index] == ',') {
            index++;
            continue;
        }
        if (index < vocab_json.size() && vocab_json[index] == '}') {
            return vocab;
        }
        throw std::runtime_error("invalid Bonsai tokenizer vocab separator");
    }

    throw std::runtime_error("unterminated Bonsai tokenizer vocab");
}

BonsaiTokenizerMerge parse_merge_string(const std::string& value) {
    const size_t separator = value.find(' ');
    if (separator == std::string::npos || separator + 1U >= value.size()) {
        throw std::runtime_error("invalid Bonsai tokenizer merge pair");
    }
    return BonsaiTokenizerMerge {
        value.substr(0, separator),
        value.substr(separator + 1U),
    };
}

BonsaiTokenizerMerge parse_merge_array(const std::string& merges_json, size_t* index) {
    require_index(merges_json, *index, "merge array");
    if (merges_json[*index] != '[') {
        throw std::runtime_error("invalid Bonsai tokenizer merge array");
    }
    (*index)++;
    skip_whitespace(merges_json, index);
    const std::string first = parse_string_at(merges_json, index);
    skip_whitespace(merges_json, index);
    require_index(merges_json, *index, "merge separator");
    if (merges_json[*index] != ',') {
        throw std::runtime_error("invalid Bonsai tokenizer merge array separator");
    }
    (*index)++;
    skip_whitespace(merges_json, index);
    const std::string second = parse_string_at(merges_json, index);
    skip_whitespace(merges_json, index);
    require_index(merges_json, *index, "merge close");
    if (merges_json[*index] != ']') {
        throw std::runtime_error("invalid Bonsai tokenizer merge array close");
    }
    (*index)++;
    return BonsaiTokenizerMerge {first, second};
}

std::vector<BonsaiTokenizerMerge> parse_merge_entries(
    const std::string& merges_json,
    bool* first_entry_is_array
) {
    std::vector<BonsaiTokenizerMerge> merges;
    size_t index = 0;
    skip_whitespace(merges_json, &index);
    require_index(merges_json, index, "merges");
    if (merges_json[index] != '[') {
        throw std::runtime_error("invalid Bonsai tokenizer merges");
    }
    index++;

    bool first_entry = true;
    while (index < merges_json.size()) {
        skip_whitespace(merges_json, &index);
        if (index < merges_json.size() && merges_json[index] == ']') {
            return merges;
        }

        if (first_entry) {
            *first_entry_is_array = merges_json[index] == '[';
            first_entry = false;
        }
        if (merges_json[index] == '[') {
            merges.push_back(parse_merge_array(merges_json, &index));
        } else if (merges_json[index] == '"') {
            merges.push_back(parse_merge_string(parse_string_at(merges_json, &index)));
        } else {
            throw std::runtime_error("invalid Bonsai tokenizer merge entry");
        }

        skip_whitespace(merges_json, &index);
        if (index < merges_json.size() && merges_json[index] == ',') {
            index++;
            continue;
        }
        if (index < merges_json.size() && merges_json[index] == ']') {
            return merges;
        }
        throw std::runtime_error("invalid Bonsai tokenizer merge separator");
    }

    throw std::runtime_error("unterminated Bonsai tokenizer merges");
}

std::vector<BonsaiTokenizerAddedToken> parse_added_tokens(const std::string& tokenizer_json) {
    const size_t start = find_value_start(tokenizer_json, "added_tokens");
    if (start == std::string::npos) {
        return {};
    }
    require_index(tokenizer_json, start, "added_tokens");
    if (tokenizer_json[start] != '[') {
        throw std::runtime_error("invalid Bonsai tokenizer added_tokens");
    }

    const size_t end = container_end(tokenizer_json, start, '[', ']');
    const std::string added_tokens_json = tokenizer_json.substr(start, end - start);
    size_t index = 0;
    skip_whitespace(added_tokens_json, &index);
    index++;

    std::vector<BonsaiTokenizerAddedToken> added_tokens;
    while (index < added_tokens_json.size()) {
        skip_whitespace(added_tokens_json, &index);
        if (index < added_tokens_json.size() && added_tokens_json[index] == ']') {
            return added_tokens;
        }
        if (added_tokens_json[index] != '{') {
            throw std::runtime_error("invalid Bonsai tokenizer added token object");
        }
        const size_t object_end = container_end(added_tokens_json, index, '{', '}');
        const std::string object = added_tokens_json.substr(index, object_end - index);

        BonsaiTokenizerAddedToken token;
        if (!optional_string_value(object, "content", &token.content) ||
            !optional_int_value(object, "id", &token.id)) {
            throw std::runtime_error("invalid Bonsai tokenizer added token");
        }
        (void) optional_bool_value(object, "special", &token.special);
        added_tokens.push_back(token);
        index = object_end;

        skip_whitespace(added_tokens_json, &index);
        if (index < added_tokens_json.size() && added_tokens_json[index] == ',') {
            index++;
            continue;
        }
        if (index < added_tokens_json.size() && added_tokens_json[index] == ']') {
            return added_tokens;
        }
        throw std::runtime_error("invalid Bonsai tokenizer added_tokens separator");
    }

    throw std::runtime_error("unterminated Bonsai tokenizer added_tokens");
}

bool starts_with(const std::string& value, const std::string& prefix) {
    return value.size() >= prefix.size() && value.substr(0, prefix.size()) == prefix;
}

std::unordered_map<std::string, int> merge_ranks(const BonsaiTokenizerData& data) {
    std::unordered_map<std::string, int> ranks;
    ranks.reserve(data.merges.size());
    for (size_t index = 0; index < data.merges.size(); index++) {
        ranks.emplace(
            data.merges[index].first + '\n' + data.merges[index].second,
            static_cast<int>(index)
        );
    }
    return ranks;
}

std::vector<std::string> utf8_scalars(const std::string& value) {
    std::vector<std::string> scalars;
    size_t index = 0;
    while (index < value.size()) {
        const unsigned char byte = static_cast<unsigned char>(value[index]);
        size_t length = 1;
        if ((byte & 0x80U) == 0U) {
            length = 1;
        } else if ((byte & 0xE0U) == 0xC0U) {
            length = 2;
        } else if ((byte & 0xF0U) == 0xE0U) {
            length = 3;
        } else if ((byte & 0xF8U) == 0xF0U) {
            length = 4;
        } else {
            throw std::runtime_error("invalid Bonsai tokenizer UTF-8 token");
        }
        if (index + length > value.size()) {
            throw std::runtime_error("truncated Bonsai tokenizer UTF-8 token");
        }
        scalars.push_back(value.substr(index, length));
        index += length;
    }
    return scalars;
}

std::vector<std::string> byte_encoder_table() {
    std::vector<uint32_t> bytes;
    for (uint32_t value = 33; value <= 126; value++) {
        bytes.push_back(value);
    }
    for (uint32_t value = 161; value <= 172; value++) {
        bytes.push_back(value);
    }
    for (uint32_t value = 174; value <= 255; value++) {
        bytes.push_back(value);
    }

    std::unordered_set<uint32_t> present(bytes.begin(), bytes.end());
    std::vector<uint32_t> codepoints = bytes;
    uint32_t extra = 0;
    for (uint32_t value = 0; value <= 255; value++) {
        if (present.find(value) == present.end()) {
            bytes.push_back(value);
            codepoints.push_back(256U + extra);
            extra++;
        }
    }

    std::vector<std::string> table(256);
    for (size_t index = 0; index < bytes.size(); index++) {
        table[bytes[index]] = utf8_codepoint(codepoints[index]);
    }
    return table;
}

const std::vector<std::string>& byte_encoder() {
    static const std::vector<std::string> table = byte_encoder_table();
    return table;
}

std::string byte_encode_token(const std::string& token) {
    const std::vector<std::string>& table = byte_encoder();
    std::string output;
    for (unsigned char byte : token) {
        output += table[byte];
    }
    return output;
}

bool is_ascii_letter(unsigned char byte) {
    return (byte >= 'A' && byte <= 'Z') || (byte >= 'a' && byte <= 'z');
}

bool is_ascii_digit(unsigned char byte) {
    return byte >= '0' && byte <= '9';
}

bool is_ascii_space(unsigned char byte) {
    return byte == ' ' || byte == '\n' || byte == '\t' || byte == '\r' || byte == '\f';
}

bool starts_with_at(const std::string& value, size_t index, const std::string& prefix) {
    return index + prefix.size() <= value.size() &&
        value.compare(index, prefix.size(), prefix) == 0;
}

std::vector<std::string> byte_level_pretokens(const std::string& text) {
    std::vector<std::string> tokens;
    size_t index = 0;
    const std::vector<std::string> contractions = {"'s", "'t", "'re", "'ve", "'m", "'ll", "'d"};
    while (index < text.size()) {
        bool matched_contraction = false;
        for (const std::string& contraction : contractions) {
            if (starts_with_at(text, index, contraction)) {
                tokens.push_back(byte_encode_token(contraction));
                index += contraction.size();
                matched_contraction = true;
                break;
            }
        }
        if (matched_contraction) {
            continue;
        }

        const size_t start = index;
        const unsigned char current = static_cast<unsigned char>(text[index]);
        if (current == ' ' && index + 1U < text.size()) {
            const unsigned char next = static_cast<unsigned char>(text[index + 1U]);
            if (is_ascii_letter(next)) {
                index += 2U;
                while (index < text.size() &&
                       is_ascii_letter(static_cast<unsigned char>(text[index]))) {
                    index++;
                }
                tokens.push_back(byte_encode_token(text.substr(start, index - start)));
                continue;
            }
            if (is_ascii_digit(next)) {
                index += 2U;
                while (index < text.size() &&
                       is_ascii_digit(static_cast<unsigned char>(text[index]))) {
                    index++;
                }
                tokens.push_back(byte_encode_token(text.substr(start, index - start)));
                continue;
            }
            if (!is_ascii_space(next)) {
                index += 2U;
                while (index < text.size()) {
                    const unsigned char value = static_cast<unsigned char>(text[index]);
                    if (is_ascii_space(value) || is_ascii_letter(value) || is_ascii_digit(value)) {
                        break;
                    }
                    index++;
                }
                tokens.push_back(byte_encode_token(text.substr(start, index - start)));
                continue;
            }
        }

        if (is_ascii_letter(current)) {
            index++;
            while (index < text.size() &&
                   is_ascii_letter(static_cast<unsigned char>(text[index]))) {
                index++;
            }
        } else if (is_ascii_digit(current)) {
            index++;
            while (index < text.size() &&
                   is_ascii_digit(static_cast<unsigned char>(text[index]))) {
                index++;
            }
        } else if (is_ascii_space(current)) {
            index++;
            while (index < text.size() &&
                   is_ascii_space(static_cast<unsigned char>(text[index]))) {
                index++;
            }
            if (index < text.size() && index > start + 1U) {
                index--;
            }
        } else {
            index++;
            while (index < text.size()) {
                const unsigned char value = static_cast<unsigned char>(text[index]);
                if (is_ascii_space(value) || is_ascii_letter(value) || is_ascii_digit(value)) {
                    break;
                }
                index++;
            }
        }
        tokens.push_back(byte_encode_token(text.substr(start, index - start)));
    }
    return tokens;
}

std::vector<std::string> bpe_tokens(
    const std::string& token,
    const std::unordered_map<std::string, int>& ranks
) {
    std::vector<std::string> word = utf8_scalars(token);
    if (word.size() <= 1U) {
        return word;
    }

    while (true) {
        int best_rank = std::numeric_limits<int>::max();
        size_t best_index = std::numeric_limits<size_t>::max();
        for (size_t index = 0; index + 1U < word.size(); index++) {
            const auto found = ranks.find(word[index] + '\n' + word[index + 1U]);
            if (found != ranks.end() && found->second < best_rank) {
                best_rank = found->second;
                best_index = index;
            }
        }
        if (best_index == std::numeric_limits<size_t>::max()) {
            break;
        }

        std::vector<std::string> merged;
        merged.reserve(word.size() - 1U);
        for (size_t index = 0; index < word.size(); index++) {
            if (index == best_index) {
                merged.push_back(word[index] + word[index + 1U]);
                index++;
            } else {
                merged.push_back(word[index]);
            }
        }
        word = merged;
        if (word.size() == 1U) {
            break;
        }
    }
    return word;
}

void append_token_id(
    const BonsaiTokenizerData& data,
    const std::string& token,
    std::vector<int32_t>* ids
) {
    const auto added = data.added_token_ids.find(token);
    if (added != data.added_token_ids.end()) {
        ids->push_back(added->second);
        return;
    }
    const auto vocab = data.vocab.find(token);
    if (vocab != data.vocab.end()) {
        ids->push_back(vocab->second);
        return;
    }

    for (unsigned char byte : token) {
        std::ostringstream key;
        key << "<0x";
        const char* digits = "0123456789ABCDEF";
        key << digits[(byte >> 4U) & 0x0FU] << digits[byte & 0x0FU] << ">";
        const auto fallback = data.vocab.find(key.str());
        if (fallback == data.vocab.end()) {
            throw std::runtime_error("Bonsai tokenizer token is missing from vocab: " + token);
        }
        ids->push_back(fallback->second);
    }
}

void encode_regular_segment(
    const BonsaiTokenizerData& data,
    const std::unordered_map<std::string, int>& ranks,
    const std::string& segment,
    std::vector<int32_t>* ids
) {
    for (const std::string& pretoken : byte_level_pretokens(segment)) {
        for (const std::string& token : bpe_tokens(pretoken, ranks)) {
            append_token_id(data, token, ids);
        }
    }
}

} // namespace

BonsaiTokenizerMetadata bonsai_load_tokenizer_metadata(const std::string& tokenizer_path) {
    return bonsai_load_tokenizer_data(tokenizer_path).metadata;
}

BonsaiTokenizerData bonsai_load_tokenizer_data(const std::string& tokenizer_path) {
    const std::string tokenizer_config_path = bonsai_join_path(
        tokenizer_path,
        "tokenizer_config.json"
    );
    const std::string tokenizer_json_path = bonsai_join_path(tokenizer_path, "tokenizer.json");
    bonsai_require_file(tokenizer_config_path, "tokenizer config");
    bonsai_require_file(tokenizer_json_path, "tokenizer");

    const std::string tokenizer_config_json = bonsai_read_text_file(tokenizer_config_path);
    const std::string tokenizer_json = bonsai_read_text_file(tokenizer_json_path);
    const std::string model_json = extract_container_for_key(tokenizer_json, "model", '{', '}');
    const std::string vocab_json = extract_container_for_key(model_json, "vocab", '{', '}');
    const std::string merges_json = extract_container_for_key(model_json, "merges", '[', ']');

    BonsaiTokenizerData data;
    BonsaiTokenizerMetadata& metadata = data.metadata;
    (void) optional_string_value(
        tokenizer_config_json,
        "tokenizer_class",
        &metadata.original_tokenizer_class
    );
    metadata.qwen_tokenizer_class_rewritten = starts_with(
        metadata.original_tokenizer_class,
        "Qwen"
    );
    metadata.runtime_tokenizer_class = metadata.qwen_tokenizer_class_rewritten
        ? "GPT2Tokenizer"
        : metadata.original_tokenizer_class;
    (void) optional_string_value(model_json, "type", &metadata.model_type);

    bool merges_are_pair_arrays = false;
    data.vocab = parse_vocab_entries(vocab_json);
    data.merges = parse_merge_entries(merges_json, &merges_are_pair_arrays);
    data.added_tokens = parse_added_tokens(tokenizer_json);
    for (const BonsaiTokenizerAddedToken& token : data.added_tokens) {
        data.added_token_ids[token.content] = token.id;
    }
    metadata.vocab_size = static_cast<uint64_t>(data.vocab.size());
    metadata.merge_count = static_cast<uint64_t>(data.merges.size());
    metadata.merge_pairs_need_normalization = merges_are_pair_arrays;

    const BonsaiQwenPromptSpec prompt_spec = bonsai_qwen_prompt_spec();
    (void) optional_token_string(tokenizer_config_json, "eos_token", &metadata.eos_token);
    if (!metadata.eos_token.empty()) {
        const auto eos_added_id = data.added_token_ids.find(metadata.eos_token);
        if (eos_added_id != data.added_token_ids.end()) {
            metadata.eos_token_id = eos_added_id->second;
        } else if (const auto eos_id = data.vocab.find(metadata.eos_token);
                   eos_id != data.vocab.end()) {
            metadata.eos_token_id = eos_id->second;
        }
    }
    if (metadata.eos_token_id == 0) {
        metadata.eos_token_id = prompt_spec.eos_token_id;
    }

    (void) optional_token_string(tokenizer_config_json, "pad_token", &metadata.pad_token);
    if (metadata.pad_token.empty()) {
        const std::string special_tokens_map_path = bonsai_join_path(
            tokenizer_path,
            "special_tokens_map.json"
        );
        if (bonsai_path_is_regular_file(special_tokens_map_path)) {
            const std::string special_tokens_json = bonsai_read_text_file(special_tokens_map_path);
            (void) optional_token_string(special_tokens_json, "pad_token", &metadata.pad_token);
        }
    }
    if (!metadata.pad_token.empty()) {
        const auto pad_added_id = data.added_token_ids.find(metadata.pad_token);
        if (pad_added_id != data.added_token_ids.end()) {
            metadata.pad_token_id = pad_added_id->second;
        } else if (const auto pad_id = data.vocab.find(metadata.pad_token);
                   pad_id != data.vocab.end()) {
            metadata.pad_token_id = pad_id->second;
        }
    }
    if (metadata.pad_token_id == 0) {
        metadata.pad_token_id = metadata.eos_token_id != 0
            ? metadata.eos_token_id
            : prompt_spec.pad_token_id;
    }

    return data;
}

uint64_t bonsai_tokenizer_metadata_checksum(const BonsaiTokenizerMetadata& metadata) {
    uint64_t checksum = metadata.vocab_size * 3U + metadata.merge_count * 5U;
    checksum += static_cast<uint64_t>(metadata.pad_token_id) * 7U;
    checksum += static_cast<uint64_t>(metadata.eos_token_id) * 11U;
    checksum += metadata.qwen_tokenizer_class_rewritten ? 13U : 0U;
    checksum += metadata.merge_pairs_need_normalization ? 17U : 0U;
    for (char value : metadata.runtime_tokenizer_class) {
        checksum += static_cast<uint64_t>(static_cast<unsigned char>(value));
    }
    for (char value : metadata.model_type) {
        checksum += static_cast<uint64_t>(static_cast<unsigned char>(value)) * 2U;
    }
    return checksum;
}

uint64_t bonsai_tokenizer_data_checksum(const BonsaiTokenizerData& data) {
    uint64_t checksum = bonsai_tokenizer_metadata_checksum(data.metadata);
    checksum += static_cast<uint64_t>(data.vocab.size()) * 29U;
    checksum += static_cast<uint64_t>(data.merges.size()) * 31U;
    checksum += static_cast<uint64_t>(data.added_tokens.size()) * 37U;
    const size_t merge_limit = std::min<size_t>(data.merges.size(), 16);
    for (size_t index = 0; index < merge_limit; index++) {
        for (char value : data.merges[index].first) {
            checksum += static_cast<uint64_t>(static_cast<unsigned char>(value));
        }
        for (char value : data.merges[index].second) {
            checksum += static_cast<uint64_t>(static_cast<unsigned char>(value)) * 2U;
        }
    }
    return checksum;
}

std::vector<int32_t> bonsai_tokenizer_encode(
    const BonsaiTokenizerData& data,
    const std::string& text
) {
    const std::unordered_map<std::string, int> ranks = merge_ranks(data);
    std::vector<int32_t> ids;
    size_t index = 0;

    while (index < text.size()) {
        size_t best_position = std::string::npos;
        const BonsaiTokenizerAddedToken* best_token = nullptr;
        for (const BonsaiTokenizerAddedToken& token : data.added_tokens) {
            if (token.content.empty()) {
                continue;
            }
            const size_t position = text.find(token.content, index);
            if (position != std::string::npos &&
                (best_token == nullptr ||
                 position < best_position ||
                 (position == best_position && token.content.size() > best_token->content.size()))) {
                best_position = position;
                best_token = &token;
            }
        }

        if (best_token == nullptr) {
            encode_regular_segment(data, ranks, text.substr(index), &ids);
            break;
        }

        if (best_position > index) {
            encode_regular_segment(
                data,
                ranks,
                text.substr(index, best_position - index),
                &ids
            );
        }
        ids.push_back(best_token->id);
        index = best_position + best_token->content.size();
    }

    return ids;
}
