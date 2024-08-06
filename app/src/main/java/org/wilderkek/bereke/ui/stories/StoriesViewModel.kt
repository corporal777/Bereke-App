package org.wilderkek.bereke.ui.stories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.wilderkek.bereke.di.data.AppData
import org.wilderkek.bereke.model.response.StoriesModel
import org.wilderkek.bereke.model.response.StoryModel
import org.wilderkek.bereke.ui.base.BaseViewModel

class StoriesViewModel(private val appData: AppData) : BaseViewModel(appData) {

    private var position = 0
    private val storiesList = arrayListOf<StoryModel>()
    private val _story = MutableLiveData<StoryModel>()
    val story : LiveData<StoryModel> get() = _story


    fun setStories(storiesModel : StoriesModel){
        storiesList.addAll(storiesModel.stories)
        _story.value = storiesList[position]
    }

    fun onNextStory() {
        if (position < storiesList.size - 1){
            position += 1
            _story.value = storiesList[position]
        }
    }

    fun onPrevStory() {
        if (position > 0){
            position -= 1
            _story.value = storiesList[position]
        }
    }

    fun getStoriesCount() = storiesList.size
}