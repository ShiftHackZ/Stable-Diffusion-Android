package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.Supporter

/**
 * Executes the `function` step in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
fun interface SupportersRemoteDataSource {

    suspend fun fetch(): List<Supporter>
}
