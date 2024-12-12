package com.saukikikiki.zerostunt.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    private val _misi1 = MutableLiveData(Mission(1, "Latihan Mendangak", "Beritahu Dia untuk belajar mendangak", false))
    val misi1: LiveData<Mission> = _misi1

    private val _misi2 = MutableLiveData(Mission(2, "Perlihatkan buku berwarna", "Mulai perkenalkan warna pada Dia", false))
    val misi2: LiveData<Mission> = _misi2

    private val _misi3 = MutableLiveData(Mission(3, "Ajak bercanda", "Buat Dia tertawa dengan bermain", false))
    val misi3: LiveData<Mission> = _misi3

    private val _misiBaru = MutableLiveData<List<Mission>>(emptyList())
    val misiBaru: LiveData<List<Mission>> = _misiBaru

    private var newMissionsGenerated = false

    fun updateMissionStatus(missionId: Int, isChecked: Boolean) {
        when (missionId) {
            1 -> _misi1.value = _misi1.value?.copy(isFinished = isChecked)
            2 -> _misi2.value = _misi2.value?.copy(isFinished = isChecked)
            3 -> _misi3.value = _misi3.value?.copy(isFinished = isChecked)


            in 4..6 -> {
                val currentMisiBaru = _misiBaru.value?.toMutableList() ?: mutableListOf()
                val updatedMissions = currentMisiBaru.map {
                    if (it.id == missionId) it.copy(isFinished = isChecked)
                    else it
                }
                _misiBaru.value = updatedMissions
            }
        }


        if (!newMissionsGenerated &&
            _misi1.value?.isFinished == true &&
            _misi2.value?.isFinished == true &&
            _misi3.value?.isFinished == true
        ) {
            val newMissions = listOf(
                Mission(4, "Misi Baru 1", "Pengenalan Bentuk", false),
                Mission(5, "Misi Baru 2", "Stimulasi Motorik", false),
                Mission(6, "Misi Baru 3", "Ceritakan Dongeng", false)
            )
            _misiBaru.value = newMissions
            newMissionsGenerated = true
        }
    }


    fun resetMissions() {
        _misi1.value = _misi1.value?.copy(isFinished = false)
        _misi2.value = _misi2.value?.copy(isFinished = false)
        _misi3.value = _misi3.value?.copy(isFinished = false)
        _misiBaru.value = emptyList()
        newMissionsGenerated = false
    }
}

data class Mission(
    val id: Int,
    val title: String,
    val description: String,
    val isFinished: Boolean
)