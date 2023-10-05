package tools.forma.sample.feature.characters.list.impl.domain.model

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import tools.forma.sample.core.mvvm.library.lifecycle.SingleLiveData
import tools.forma.sample.core.network.library.NetworkState
import tools.forma.sample.feature.characters.core.api.domain.model.ICharacter

interface ICharactersListViewModel {
    val networkState: LiveData<NetworkState>?
    val event: SingleLiveData<ICharactersListViewEvent>
    val data: LiveData<PagedList<ICharacter>>
    val state: LiveData<ICharactersListViewState>?

    /**
     * Refresh characters fetch them again and update the list.
     */
    fun refreshLoadedCharactersList()

    /**
     * Retry last fetch operation to add characters into list.
     */
    fun retryAddCharactersList()

    /**
     * Send interaction event for open character detail view from selected character.
     *
     * @param characterId Character identifier.
     */
    fun openCharacterDetail(characterId: Long)
}