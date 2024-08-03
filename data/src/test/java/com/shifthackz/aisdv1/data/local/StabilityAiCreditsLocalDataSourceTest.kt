package com.shifthackz.aisdv1.data.local

import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.junit.Test

class StabilityAiCreditsLocalDataSourceTest {

    private val stubSubject = BehaviorSubject.createDefault(0f)

    private val localDataSource = StabilityAiCreditsLocalDataSource(stubSubject)

    @Test
    fun `given attempt to get, then save and get, expected default value, save complete, then saved value`() {
        localDataSource
            .get()
            .test()
            .assertNoErrors()
            .assertValue(0f)
            .await()
            .assertComplete()

        localDataSource
            .save(5598f)
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()

        localDataSource
            .get()
            .test()
            .assertNoErrors()
            .assertValue(5598f)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to observe changes, value changed from default to another, expected default value, save complete, then saved value`() {
        val stubObserver = localDataSource.observe().test()

        stubObserver
            .assertNoErrors()
            .assertValueAt(0, 0f)

        localDataSource
            .save(5598f)
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()

        stubObserver
            .assertNoErrors()
            .assertValueAt(1, 5598f)
    }
}
