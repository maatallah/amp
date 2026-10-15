package com.automusicplayer.car

import androidx.car.app.CarAppService
import androidx.car.app.Session
import androidx.car.app.Screen
import androidx.car.app.SessionInfo
import androidx.car.app.validation.HostValidator
import androidx.car.app.model.*
import com.automusicplayer.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AutoCarAppService : CarAppService() {
    override fun createHostValidator(): HostValidator {
        return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
    }

    override fun onCreateSession(sessionInfo: SessionInfo): Session {
        return AutoSession()
    }
}

class AutoSession : Session() {
    override fun onCreateScreen(intent: android.content.Intent): Screen {
        return RootMusicScreen(carContext)
    }
}

class RootMusicScreen(carContext: androidx.car.app.CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        val listBuilder = ItemList.Builder()

        val categories = listOf("Artists", "Albums", "Playlists")
        for (category in categories) {
            listBuilder.addItem(
                Row.Builder()
                    .setTitle(category)
                    .setBrowsable(true)
                    .setOnClickListener {
                        screenManager.push(CatalogItemScreen(carContext, category))
                    }
                    .build()
            )
        }

        return ListTemplate.Builder()
            .setSingleList(listBuilder.build())
            .setTitle("Auto Music Player")
            .build()
    }
}

class CatalogItemScreen(carContext: androidx.car.app.CarContext, private val categoryType: String) : Screen(carContext) {
    private var itemsList = listOf<String>("Loading...")

    init {
        loadDataFromDatabase()
    }

    private fun loadDataFromDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getInstance(carContext)
            val dao = db.musicDao()
            val tracks = dao.getAllTracks()
            
            itemsList = when (categoryType) {
                "Artists" -> tracks.map { it.title }.distinct()
                "Albums" -> tracks.map { it.title }.distinct()
                else -> tracks.map { it.title }
            }
            if (itemsList.isEmpty()) {
                itemsList = listOf("No items found in $categoryType")
            }
            withContext(Dispatchers.Main) {
                invalidate()
            }
        }
    }

    override fun onGetTemplate(): Template {
        val listBuilder = ItemList.Builder()

        for (item in itemsList) {
            listBuilder.addItem(
                Row.Builder()
                    .setTitle(item)
                    .addText("Tap to play")
                    .setOnClickListener {
                        // Play audio item
                    }
                    .build()
            )
        }

        return ListTemplate.Builder()
            .setSingleList(listBuilder.build())
            .setTitle(categoryType)
            .build()
    }
}
