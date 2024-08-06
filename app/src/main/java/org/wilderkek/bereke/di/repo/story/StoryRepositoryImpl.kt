package org.wilderkek.bereke.di.repo.story

import io.reactivex.Maybe
import org.wilderkek.bereke.api.ApiService
import org.wilderkek.bereke.model.response.StoriesModel

class StoryRepositoryImpl(private val apiService: ApiService) : StoryRepository {
    override fun getStoriesList(): Maybe<List<StoriesModel>> = apiService.getStoriesList()
}