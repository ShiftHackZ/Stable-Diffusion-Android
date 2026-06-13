cmake_minimum_required(VERSION 3.22.1)

project(sdai_sdcpp_runtime LANGUAGES C CXX)

option(SDAI_SDCPP_ENABLE_OPENCL "Build stable-diffusion.cpp with OpenCL backend" ON)
option(SDAI_SDCPP_ENABLE_VULKAN "Build stable-diffusion.cpp with Vulkan backend" ON)

set(SDAI_SDCPP_VENDOR_DIR ${CMAKE_CURRENT_LIST_DIR}/vendor)
set(SDAI_SDCPP_STABLE_DIFFUSION_CPP_VENDOR_DIR
    ${SDAI_SDCPP_VENDOR_DIR}/stable-diffusion.cpp
)
set(SDAI_SDCPP_VULKAN_HEADERS_DIR ${SDAI_SDCPP_VENDOR_DIR}/Vulkan-Headers)
set(SDAI_SDCPP_SPIRV_HEADERS_DIR ${SDAI_SDCPP_VENDOR_DIR}/SPIRV-Headers)
set(SDAI_SDCPP_OPENCL_HEADERS_DIR ${SDAI_SDCPP_VENDOR_DIR}/OpenCL-Headers)
set(SDAI_SDCPP_OPENCL_ICD_LOADER_DIR ${SDAI_SDCPP_VENDOR_DIR}/OpenCL-ICD-Loader)

if(NOT EXISTS ${SDAI_SDCPP_STABLE_DIFFUSION_CPP_VENDOR_DIR}/CMakeLists.txt)
    message(
        FATAL_ERROR
        "Missing stable-diffusion.cpp submodule. Run: git submodule update --init --recursive -- feature/sdxl/src/androidMain/cpp/vendor/stable-diffusion.cpp"
    )
endif()

if(NOT EXISTS ${SDAI_SDCPP_STABLE_DIFFUSION_CPP_VENDOR_DIR}/ggml/CMakeLists.txt)
    message(
        FATAL_ERROR
        "Missing stable-diffusion.cpp nested submodules. Run: git submodule update --init --recursive -- feature/sdxl/src/androidMain/cpp/vendor/stable-diffusion.cpp"
    )
endif()

if(SDAI_SDCPP_ENABLE_OPENCL)
    if(NOT EXISTS ${SDAI_SDCPP_OPENCL_HEADERS_DIR}/CL/cl.h)
        message(
            FATAL_ERROR
            "Missing OpenCL-Headers submodule. Run: git submodule update --init -- feature/sdxl/src/androidMain/cpp/vendor/OpenCL-Headers"
        )
    endif()

    if(NOT EXISTS ${SDAI_SDCPP_OPENCL_ICD_LOADER_DIR}/CMakeLists.txt)
        message(
            FATAL_ERROR
            "Missing OpenCL-ICD-Loader submodule. Run: git submodule update --init -- feature/sdxl/src/androidMain/cpp/vendor/OpenCL-ICD-Loader"
        )
    endif()
endif()

if(SDAI_SDCPP_ENABLE_VULKAN)
    if(NOT EXISTS ${SDAI_SDCPP_VULKAN_HEADERS_DIR}/include/vulkan/vulkan.h)
        message(
            FATAL_ERROR
            "Missing Vulkan-Headers submodule. Run: git submodule update --init -- feature/sdxl/src/androidMain/cpp/vendor/Vulkan-Headers"
        )
    endif()

    if(NOT EXISTS ${SDAI_SDCPP_SPIRV_HEADERS_DIR}/include/spirv/unified1/spirv.hpp)
        message(
            FATAL_ERROR
            "Missing SPIRV-Headers submodule. Run: git submodule update --init -- feature/sdxl/src/androidMain/cpp/vendor/SPIRV-Headers"
        )
    endif()
endif()

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_POSITION_INDEPENDENT_CODE ON)

set(BUILD_SHARED_LIBS OFF CACHE BOOL "" FORCE)
set(SD_BUILD_EXAMPLES OFF CACHE BOOL "" FORCE)
set(SD_BUILD_TESTS OFF CACHE BOOL "" FORCE)
set(SD_BUILD_SHARED_LIBS OFF CACHE BOOL "" FORCE)
set(SD_BUILD_SHARED_GGML_LIB OFF CACHE BOOL "" FORCE)
set(SD_WEBP OFF CACHE BOOL "" FORCE)
set(SD_WEBM OFF CACHE BOOL "" FORCE)
set(SD_OPENCL ${SDAI_SDCPP_ENABLE_OPENCL} CACHE BOOL "" FORCE)
set(SD_VULKAN ${SDAI_SDCPP_ENABLE_VULKAN} CACHE BOOL "" FORCE)
set(GGML_OPENMP OFF CACHE BOOL "" FORCE)

if(SDAI_SDCPP_ENABLE_OPENCL)
    set(OPENCL_ICD_LOADER_BUILD_SHARED_LIBS OFF CACHE BOOL "" FORCE)
    set(OPENCL_ICD_LOADER_BUILD_TESTING OFF CACHE BOOL "" FORCE)
    set(OPENCL_ICD_LOADER_HEADERS_DIR ${SDAI_SDCPP_OPENCL_HEADERS_DIR} CACHE PATH "" FORCE)
    set(ENABLE_OPENCL_LAYERS OFF CACHE BOOL "" FORCE)
    set(ENABLE_OPENCL_LAYERINFO OFF CACHE BOOL "" FORCE)
    add_subdirectory(
        ${SDAI_SDCPP_OPENCL_ICD_LOADER_DIR}
        ${CMAKE_CURRENT_BINARY_DIR}/OpenCL-ICD-Loader-build
    )
endif()

if(SDAI_SDCPP_ENABLE_VULKAN)
    if(ANDROID_NDK)
        file(GLOB SDAI_SDCPP_NDK_GLSLC_CANDIDATES "${ANDROID_NDK}/shader-tools/*/glslc")
        list(LENGTH SDAI_SDCPP_NDK_GLSLC_CANDIDATES SDAI_SDCPP_NDK_GLSLC_COUNT)
        if(SDAI_SDCPP_NDK_GLSLC_COUNT GREATER 0)
            list(GET SDAI_SDCPP_NDK_GLSLC_CANDIDATES 0 SDAI_SDCPP_NDK_GLSLC)
            set(Vulkan_GLSLC_EXECUTABLE ${SDAI_SDCPP_NDK_GLSLC} CACHE FILEPATH "" FORCE)
        endif()
    endif()

    include_directories(SYSTEM ${SDAI_SDCPP_VULKAN_HEADERS_DIR}/include)
    include_directories(SYSTEM ${SDAI_SDCPP_SPIRV_HEADERS_DIR}/include)
    set(Vulkan_INCLUDE_DIR ${SDAI_SDCPP_VULKAN_HEADERS_DIR}/include CACHE PATH "" FORCE)

    if(NOT Vulkan_GLSLC_EXECUTABLE OR Vulkan_GLSLC_EXECUTABLE MATCHES "NOTFOUND$")
        message(FATAL_ERROR "SDAI stable-diffusion.cpp Vulkan backend requires glslc. Install Vulkan SDK or use an Android NDK with shader-tools/glslc.")
    endif()
endif()

set(stable_diffusion_cpp_SOURCE_DIR
    ${CMAKE_CURRENT_BINARY_DIR}/stable-diffusion.cpp-src
)
set(stable_diffusion_cpp_BINARY_DIR
    ${CMAKE_CURRENT_BINARY_DIR}/stable-diffusion.cpp-build
)

file(REMOVE_RECURSE ${stable_diffusion_cpp_SOURCE_DIR})
file(
    COPY ${SDAI_SDCPP_STABLE_DIFFUSION_CPP_VENDOR_DIR}/
    DESTINATION ${stable_diffusion_cpp_SOURCE_DIR}
    PATTERN ".git" EXCLUDE
)

if(SDAI_SDCPP_ENABLE_OPENCL)
    set(SDAI_SDCPP_GGML_OPENCL_CMAKE
        ${stable_diffusion_cpp_SOURCE_DIR}/ggml/src/ggml-opencl/CMakeLists.txt
    )
    if(EXISTS ${SDAI_SDCPP_GGML_OPENCL_CMAKE})
        # Patch the build-local copy only; the pinned submodule stays clean.
        # Upstream probes the host sysroot for OpenCL, while SDAI provides
        # vendored Khronos headers and the ICD loader target.
        file(READ ${SDAI_SDCPP_GGML_OPENCL_CMAKE} SDAI_SDCPP_GGML_OPENCL_CMAKE_CONTENT)
        string(
            REPLACE
            "find_package(OpenCL REQUIRED)"
            "if(TARGET OpenCL)\n    set(OpenCL_LIBRARIES OpenCL)\n    set(OpenCL_INCLUDE_DIRS ${SDAI_SDCPP_OPENCL_HEADERS_DIR})\nelse()\n    find_package(OpenCL REQUIRED)\nendif()"
            SDAI_SDCPP_GGML_OPENCL_CMAKE_PATCHED
            "${SDAI_SDCPP_GGML_OPENCL_CMAKE_CONTENT}"
        )
        if(NOT SDAI_SDCPP_GGML_OPENCL_CMAKE_PATCHED STREQUAL SDAI_SDCPP_GGML_OPENCL_CMAKE_CONTENT)
            file(WRITE ${SDAI_SDCPP_GGML_OPENCL_CMAKE} "${SDAI_SDCPP_GGML_OPENCL_CMAKE_PATCHED}")
        endif()
    endif()
endif()

if(SDAI_SDCPP_ENABLE_VULKAN)
    set(SDAI_SDCPP_GGML_VULKAN_CMAKE
        ${stable_diffusion_cpp_SOURCE_DIR}/ggml/src/ggml-vulkan/CMakeLists.txt
    )
    if(EXISTS ${SDAI_SDCPP_GGML_VULKAN_CMAKE})
        # Patch the build-local copy only; the pinned submodule stays clean.
        file(READ ${SDAI_SDCPP_GGML_VULKAN_CMAKE} SDAI_SDCPP_GGML_VULKAN_CMAKE_CONTENT)
        string(
            REPLACE
            "if (\${glslc_error} MATCHES"
            "if (\"\${glslc_error}\" MATCHES"
            SDAI_SDCPP_GGML_VULKAN_CMAKE_PATCHED
            "${SDAI_SDCPP_GGML_VULKAN_CMAKE_CONTENT}"
        )
        if(NOT SDAI_SDCPP_GGML_VULKAN_CMAKE_PATCHED STREQUAL SDAI_SDCPP_GGML_VULKAN_CMAKE_CONTENT)
            file(WRITE ${SDAI_SDCPP_GGML_VULKAN_CMAKE} "${SDAI_SDCPP_GGML_VULKAN_CMAKE_PATCHED}")
        endif()
    endif()

    set(SDAI_SDCPP_GGML_VULKAN_CPP
        ${stable_diffusion_cpp_SOURCE_DIR}/ggml/src/ggml-vulkan/ggml-vulkan.cpp
    )
    if(EXISTS ${SDAI_SDCPP_GGML_VULKAN_CPP})
        file(READ ${SDAI_SDCPP_GGML_VULKAN_CPP} SDAI_SDCPP_GGML_VULKAN_CPP_CONTENT)
        string(
            REPLACE
            "vkGetPhysicalDeviceFeatures2("
            "VULKAN_HPP_DEFAULT_DISPATCHER.vkGetPhysicalDeviceFeatures2("
            SDAI_SDCPP_GGML_VULKAN_CPP_PATCHED
            "${SDAI_SDCPP_GGML_VULKAN_CPP_CONTENT}"
        )
        if(NOT SDAI_SDCPP_GGML_VULKAN_CPP_PATCHED STREQUAL SDAI_SDCPP_GGML_VULKAN_CPP_CONTENT)
            file(WRITE ${SDAI_SDCPP_GGML_VULKAN_CPP} "${SDAI_SDCPP_GGML_VULKAN_CPP_PATCHED}")
        endif()
    endif()
endif()

add_subdirectory(${stable_diffusion_cpp_SOURCE_DIR} ${stable_diffusion_cpp_BINARY_DIR})

if(SDAI_SDCPP_ENABLE_VULKAN)
    set(SDAI_SDCPP_VULKAN_SHADER_DIR
        ${stable_diffusion_cpp_BINARY_DIR}/ggml/src/ggml-vulkan/vulkan-shaders.spv
    )
    add_custom_target(
        sdai_sdcpp_prepare_vulkan_shaders
        COMMAND ${CMAKE_COMMAND} -E make_directory ${SDAI_SDCPP_VULKAN_SHADER_DIR}
    )
    if(TARGET ggml-vulkan)
        add_dependencies(ggml-vulkan sdai_sdcpp_prepare_vulkan_shaders)
    endif()
endif()

add_library(sdai_sdcpp SHARED stable_diffusion_jni.cpp)

target_link_libraries(
    sdai_sdcpp
    PRIVATE
    stable-diffusion
    android
    log
)

target_compile_definitions(
    sdai_sdcpp
    PRIVATE
    SDAI_SDCPP_HAS_OPENCL=$<BOOL:${SDAI_SDCPP_ENABLE_OPENCL}>
    SDAI_SDCPP_HAS_VULKAN=$<BOOL:${SDAI_SDCPP_ENABLE_VULKAN}>
)

target_link_options(
    sdai_sdcpp
    PRIVATE
    "-Wl,-z,max-page-size=16384"
    "-Wl,-z,common-page-size=16384"
)

target_compile_options(
    sdai_sdcpp
    PRIVATE
    -Wall
    -Wextra
    -Wno-unused-parameter
)
