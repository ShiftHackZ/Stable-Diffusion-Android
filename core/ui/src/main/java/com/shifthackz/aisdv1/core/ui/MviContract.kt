@file:Suppress("unused")

package com.shifthackz.aisdv1.core.ui

interface MviState
interface MviIntent
interface MviEffect

object EmptyState : MviState
object EmptyIntent : MviIntent
object EmptyEffect : MviEffect
