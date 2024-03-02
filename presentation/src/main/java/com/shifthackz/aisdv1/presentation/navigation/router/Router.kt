package com.shifthackz.aisdv1.presentation.navigation.router

import com.shifthackz.aisdv1.presentation.navigation.NavigationEffect
import io.reactivex.rxjava3.core.Observable

interface Router<T : NavigationEffect>  {
    fun observe(): Observable<T>
}
