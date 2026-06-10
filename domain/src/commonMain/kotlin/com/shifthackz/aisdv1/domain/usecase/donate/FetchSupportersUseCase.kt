package com.shifthackz.aisdv1.domain.usecase.donate

import com.shifthackz.aisdv1.domain.entity.Supporter

/**
 * Executes the `function` step in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
fun interface FetchSupportersUseCase {

    suspend operator fun invoke(): List<Supporter>
}
