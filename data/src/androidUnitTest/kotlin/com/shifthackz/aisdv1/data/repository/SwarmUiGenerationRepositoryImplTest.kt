package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.mocks.mockAiGenerationResult
import com.shifthackz.aisdv1.data.mocks.mockImageToImagePayload
import com.shifthackz.aisdv1.data.mocks.mockTextToImagePayload
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.datasource.SwarmUiGenerationDataSource
import com.shifthackz.aisdv1.domain.datasource.SwarmUiModelsRemoteDataSource
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationStore
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.preference.SessionPreference
import com.shifthackz.aisdv1.network.exception.SwarmUiBadSessionException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SwarmUiGenerationRepositoryImplTest {

    private val stubException = Throwable("Something went wrong.")
    private val stubMediaStoreGateway = mockk<MediaStoreGateway>()
    private val stubLocalDataSource = mockk<GenerationResultDataSource.Local>()
    private val stubRemoteDataSource = mockk<SwarmUiGenerationDataSource.Remote>()
    private val stubSwarmSessionRemote = mockk<SwarmUiModelsRemoteDataSource>()
    private val stubPreferenceManager = mockk<PreferenceManager>()
    private val stubSessionPreference = mockk<SessionPreference>(relaxed = true)
    private val stubAuthorizationStore = mockk<AuthorizationStore>()
    private val stubBackgroundWorkObserver = mockk<BackgroundWorkObserver>()

    private val repository = SwarmUiGenerationRepositoryImpl(
        mediaStoreGateway = stubMediaStoreGateway,
        localDataSource = stubLocalDataSource,
        remoteDataSource = stubRemoteDataSource,
        sessionPreference = stubSessionPreference,
        authorizationStore = stubAuthorizationStore,
        swarmSessionRemoteDataSource = stubSwarmSessionRemote,
        preferenceManager = stubPreferenceManager,
        backgroundWorkObserver = stubBackgroundWorkObserver,
    )

    @Before
    fun initialize() {
        every {
            stubBackgroundWorkObserver.hasActiveTasks()
        } returns false

        every {
            stubPreferenceManager.autoSaveAiResults
        } returns false

        every {
            stubPreferenceManager.swarmUiServerUrl
        } returns SWARM_URL

        every {
            stubPreferenceManager.swarmUiModel
        } returns MODEL

        every {
            stubAuthorizationStore.getAuthorizationCredentials()
        } returns AuthorizationCredentials.None

        every {
            stubSessionPreference.swarmUiSessionId
        } returns SESSION_ID
    }

    @Test
    fun `given attempt to check api availability with cached session, expected complete value`() = runTest {
        repository.checkApiAvailability()

        coVerify(exactly = 0) {
            stubSwarmSessionRemote.getNewSession(any(), any())
        }
    }

    @Test
    fun `given attempt to check api availability with empty session, expected new session requested`() = runTest {
        every {
            stubSessionPreference.swarmUiSessionId
        } returns ""

        coEvery {
            stubSwarmSessionRemote.getNewSession(SWARM_URL, AuthorizationCredentials.None)
        } returns RENEWED_SESSION_ID

        repository.checkApiAvailability()

        coVerify {
            stubSwarmSessionRemote.getNewSession(SWARM_URL, AuthorizationCredentials.None)
        }
        verify {
            stubSessionPreference.swarmUiSessionId = RENEWED_SESSION_ID
        }
    }

    @Test
    fun `given attempt to check api availability by url, expected forced new session requested`() = runTest {
        coEvery {
            stubSwarmSessionRemote.getNewSession(CHECK_URL, AuthorizationCredentials.None)
        } returns RENEWED_SESSION_ID

        repository.checkApiAvailability(CHECK_URL)

        coVerify {
            stubSwarmSessionRemote.getNewSession(CHECK_URL, AuthorizationCredentials.None)
        }
        verify {
            stubSessionPreference.swarmUiSessionId = RENEWED_SESSION_ID
        }
    }

    @Test
    fun `given attempt to check api availability by url, remote throws exception, expected error value`() = runTest {
        coEvery {
            stubSwarmSessionRemote.getNewSession(CHECK_URL, AuthorizationCredentials.None)
        } throws stubException

        val actual = runCatching { repository.checkApiAvailability(CHECK_URL) }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to generate from text, remote returns result, expected valid domain model value`() = runTest {
        coEvery {
            stubRemoteDataSource.textToImage(
                SWARM_URL,
                SESSION_ID,
                MODEL,
                AuthorizationCredentials.None,
                mockTextToImagePayload,
            )
        } returns mockAiGenerationResult

        val actual = repository.generateFromText(mockTextToImagePayload)

        Assert.assertEquals(mockAiGenerationResult, actual)
    }

    @Test
    fun `given attempt to generate from text with bad session, expected session renewed and generation retried`() =
        runTest {
            coEvery {
                stubRemoteDataSource.textToImage(
                    SWARM_URL,
                    SESSION_ID,
                    MODEL,
                    AuthorizationCredentials.None,
                    mockTextToImagePayload,
                )
            } throws SwarmUiBadSessionException()

            coEvery {
                stubSwarmSessionRemote.getNewSession(SWARM_URL, AuthorizationCredentials.None)
            } returns RENEWED_SESSION_ID

            coEvery {
                stubRemoteDataSource.textToImage(
                    SWARM_URL,
                    RENEWED_SESSION_ID,
                    MODEL,
                    AuthorizationCredentials.None,
                    mockTextToImagePayload,
                )
            } returns mockAiGenerationResult

            val actual = repository.generateFromText(mockTextToImagePayload)

            Assert.assertEquals(mockAiGenerationResult, actual)
            verify {
                stubSessionPreference.swarmUiSessionId = RENEWED_SESSION_ID
            }
        }

    @Test
    fun `given attempt to generate from text, remote throws exception, expected error value`() = runTest {
        coEvery {
            stubRemoteDataSource.textToImage(any(), any(), any(), any(), any())
        } throws stubException

        val actual = runCatching { repository.generateFromText(mockTextToImagePayload) }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to generate from image, remote returns result, expected valid domain model value`() = runTest {
        coEvery {
            stubRemoteDataSource.imageToImage(
                SWARM_URL,
                SESSION_ID,
                MODEL,
                AuthorizationCredentials.None,
                mockImageToImagePayload,
            )
        } returns mockAiGenerationResult

        val actual = repository.generateFromImage(mockImageToImagePayload)

        Assert.assertEquals(mockAiGenerationResult, actual)
    }

    @Test
    fun `given attempt to generate from image, remote throws exception, expected error value`() = runTest {
        coEvery {
            stubRemoteDataSource.imageToImage(any(), any(), any(), any(), any())
        } throws stubException

        val actual = runCatching { repository.generateFromImage(mockImageToImagePayload) }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    private companion object {
        const val SWARM_URL = "http://192.168.0.1:7801"
        const val CHECK_URL = "https://5598.is.my.favourite.com"
        const val SESSION_ID = "5598"
        const val RENEWED_SESSION_ID = "151297"
        const val MODEL = "OpenStableDiffusion"
    }
}
