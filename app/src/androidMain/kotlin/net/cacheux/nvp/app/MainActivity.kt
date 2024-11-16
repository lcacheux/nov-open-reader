package net.cacheux.nvp.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import net.cacheux.nvp.app.repository.ActivityRequirer
import net.cacheux.nvp.model.DoseGroup
import net.cacheux.nvp.ui.MainScreen
import net.cacheux.nvp.ui.SideMenuParams
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        const val CREATE_FILE = 16995
    }

    @Inject
    lateinit var repository: PenInfoRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: MainScreenViewModel by viewModels()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen(
                        doseList = DoseGroup.createDoseGroups(viewModel.doseList.collectAsState(listOf()).value).reversed(),
                        message = viewModel.getReadError().collectAsState().value,

                        loading = viewModel.isReading().collectAsState().value,
                        onDismissMessage = viewModel::onDismissMessage,
                        storeAvailable = viewModel.store.collectAsState().value != null,

                        onSaveStore = {
                            saveToFile()
                        },
                        sideMenuParams = SideMenuParams(
                            penList = viewModel.getPenList().collectAsState(listOf()).value.map { it.serial },
                            selectedPen = viewModel.getCurrentPen().collectAsState().value,
                            onItemClick = { viewModel.setCurrentPen(it) }
                        )
                    )
                }
            }
        }
    }

    private fun saveToFile() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain"
            putExtra(Intent.EXTRA_TITLE, "nvp_data.txt")

            // Optionally, specify a URI for the directory that should be opened in
            // the system file picker before your app creates the document.
            //putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri(""))
        }
        startActivityForResult(intent, CREATE_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREATE_FILE && resultCode == RESULT_OK) {
            data?.data?.also { uri ->
                contentResolver.openOutputStream(uri)?.let { output ->
                    repository.getDataStore().value?.toOutputStream(output)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (repository is ActivityRequirer) {
            (repository as ActivityRequirer).setActivity(this)
        }
    }

    override fun onStop() {
        if (repository is ActivityRequirer) {
            (repository as ActivityRequirer).setActivity(null)
        }

        super.onStop()
    }
}
