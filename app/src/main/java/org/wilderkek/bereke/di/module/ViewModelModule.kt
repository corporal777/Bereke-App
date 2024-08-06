package org.wilderkek.bereke.di.module

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.wilderkek.bereke.ui.auth.login.LoginViewModel
import org.wilderkek.bereke.ui.auth.register.RegisterViewModel
import org.wilderkek.bereke.ui.confirmCode.ConfirmCodeViewModel
import org.wilderkek.bereke.ui.profileSettings.changePassword.ChangePasswordViewModel
import org.wilderkek.bereke.ui.createNote.CreateNoteViewModel
import org.wilderkek.bereke.ui.detailNote.NoteDetailViewModel
import org.wilderkek.bereke.ui.detailNote.fullScreenImages.FullScreenImagesViewModel
import org.wilderkek.bereke.ui.editNote.EditNoteViewModel
import org.wilderkek.bereke.ui.favoriteNotes.FavoriteNotesViewModel
import org.wilderkek.bereke.ui.home.HomeViewModel
import org.wilderkek.bereke.ui.location.MetroStationsViewModel
import org.wilderkek.bereke.ui.main.MainViewModel
import org.wilderkek.bereke.ui.my.MyNotesViewModel
import org.wilderkek.bereke.ui.profile.ProfileViewModel
import org.wilderkek.bereke.ui.profileSettings.ProfileSettingsViewModel
import org.wilderkek.bereke.ui.profileSettings.changeLogin.ChangeLoginViewModel
import org.wilderkek.bereke.ui.splash.SplashViewModel
import org.wilderkek.bereke.ui.stories.StoriesViewModel
import org.wilderkek.bereke.ui.town.CitiesViewModel
import org.wilderkek.bereke.ui.town.TownViewModel

val viewModelModule = module {
    viewModel { MainViewModel(get(), get(), get()) }
    viewModel { LoginViewModel(get(), get(), get()) }
    viewModel { RegisterViewModel(get(), get(), get()) }
    viewModel { HomeViewModel(get(), get(), get(), get()) }
    viewModel { ProfileViewModel(get(), get(), get()) }
    viewModel { ProfileSettingsViewModel(get(), get(), get()) }
    viewModel { TownViewModel(get(), get(), get()) }
    viewModel { CitiesViewModel(get(), get()) }
    viewModel { CreateNoteViewModel(get(), get(), get()) }
    viewModel { MetroStationsViewModel(get(), get()) }
    viewModel { StoriesViewModel(get()) }
    viewModel { ChangePasswordViewModel(get(), get()) }
    viewModel { ChangeLoginViewModel(get(), get()) }
    viewModel { ConfirmCodeViewModel(get(), get()) }
    viewModel { SplashViewModel(get()) }
    viewModel { MyNotesViewModel(get(), get()) }
    viewModel { FavoriteNotesViewModel(get(), get()) }
    viewModel { NoteDetailViewModel(get(), get()) }
    viewModel { FullScreenImagesViewModel(get()) }
    viewModel { EditNoteViewModel(get(), get(), get()) }
}