package org.wilderkek.bereke.di.module

import org.koin.dsl.module
import org.wilderkek.bereke.di.repo.auth.AuthRepository
import org.wilderkek.bereke.di.repo.auth.AuthRepositoryImpl
import org.wilderkek.bereke.di.repo.location.LocationRepository
import org.wilderkek.bereke.di.repo.location.LocationRepositoryImpl
import org.wilderkek.bereke.di.repo.note.NotesRepository
import org.wilderkek.bereke.di.repo.note.NotesRepositoryImpl
import org.wilderkek.bereke.di.repo.story.StoryRepository
import org.wilderkek.bereke.di.repo.story.StoryRepositoryImpl
import org.wilderkek.bereke.di.repo.user.UserRepository
import org.wilderkek.bereke.di.repo.user.UserRepositoryImpl

val repoModule = module {
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<LocationRepository> { LocationRepositoryImpl(get(), get()) }
    single<NotesRepository> { NotesRepositoryImpl(get(), get()) }
    single<StoryRepository> { StoryRepositoryImpl(get()) }
}