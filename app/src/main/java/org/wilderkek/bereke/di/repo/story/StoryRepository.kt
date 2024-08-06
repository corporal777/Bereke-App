package org.wilderkek.bereke.di.repo.story

import io.reactivex.Maybe
import org.wilderkek.bereke.model.response.StoriesModel

interface StoryRepository {
    fun getStoriesList() : Maybe<List<StoriesModel>>
}