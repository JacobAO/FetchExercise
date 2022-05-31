package com.jacobao.fetchexercise

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jacobao.fetchexercise.data.HiringItem
import com.jacobao.fetchexercise.data.HiringRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val hiringRepository: HiringRepository
) : ViewModel() {

    private val _viewState = MutableStateFlow(ViewState())
    val viewState = _viewState.asStateFlow()

    /**
     * Load all hiring items from the repo.
     *
     * Normally I would never pass context into the ViewModel like this as to not leak it. For the
     * purposes of this exercise I'm pretending the JSON file is a remote API and passing in context
     * just to access it
     */
    fun loadHiringItems(context: Context) {
        viewModelScope.launch {
            _viewState.update { it.copy(loading = true) }
            hiringRepository.getHiringItemMap(context).fold(
                {
                    _viewState.update {
                        it.copy(
                            loading = false,
                            failure = true,
                            hiringItemMap = emptyMap()
                        )
                    }
                },
                { hiringItems ->
                    _viewState.update {
                        it.copy(
                            loading = false,
                            failure = false,
                            hiringItemMap = hiringItems
                        )
                    }
                }
            )
        }
    }
}

data class ViewState(
    val loading: Boolean = true,
    val failure: Boolean = false,
    val hiringItemMap: Map<Int, List<HiringItem>> = emptyMap()
)