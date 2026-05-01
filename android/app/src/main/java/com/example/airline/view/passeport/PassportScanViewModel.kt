package com.example.airline.view.passeport

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.airline.utils.MrzData
import com.example.airline.utils.MrzParser
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.util.concurrent.Executors

data class PassportScanUiState(
    val isLoading: Boolean = false,
    val isProcessingOcr: Boolean = false,
    val passportInfo: String? = null,
    val error: String? = null,
    val isCameraReady: Boolean = false,
    val imageCaptureUri: Uri? = null,
    val showCapturedImage: Boolean = false,
    val extractedMrzData: MrzData? = null
)

class PassportScanViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PassportScanUiState())
    val uiState: StateFlow<PassportScanUiState> = _uiState.asStateFlow()

    var imageCapture: ImageCapture? = null

    fun setCameraReady() {
        _uiState.update { it.copy(isCameraReady = true) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun retakePhoto() {
        _uiState.update { it.copy(showCapturedImage = false, imageCaptureUri = null, passportInfo = null, error = null) }
    }

    fun confirmPhoto(context: Context) {
        val uri = _uiState.value.imageCaptureUri ?: return
        processMrzFromUri(context, uri)
    }

    fun processMrzOcr() {
        val capture = imageCapture ?: return
        _uiState.update { it.copy(isProcessingOcr = true, error = null) }

        val photoFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "passport_${System.currentTimeMillis()}.jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        capture.takePicture(
            outputOptions,
            Executors.newSingleThreadExecutor(),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    _uiState.update { 
                        it.copy(
                            imageCaptureUri = savedUri, 
                            showCapturedImage = true, 
                            isProcessingOcr = false 
                        ) 
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    _uiState.update { 
                        it.copy(
                            error = "Erreur de capture: ${exception.message}", 
                            isProcessingOcr = false 
                        ) 
                    }
                }
            }
        )
    }

    private fun processMrzFromUri(context: Context, uri: Uri) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        try {
            val image = InputImage.fromFilePath(context, uri)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    var mrzData = MrzParser.extractFromRawText(visionText.text)
                    if (mrzData == null) {
                        // En cas d'échec de la lecture stricte du MRZ, on crée un objet vide
                        // pour permettre à l'utilisateur de passer à la page de vérification et remplir manuellement.
                        mrzData = MrzData(
                            passportNumber = "",
                            surname = "",
                            givenNames = "",
                            nationality = "",
                            dateOfBirth = "",
                            sex = "",
                            expiryDate = ""
                        )
                    }
                    
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            passportInfo = if (mrzData.passportNumber.isNotEmpty()) "MRZ extrait avec succès" else "Lecture MRZ échouée, saisie manuelle",
                            extractedMrzData = mrzData
                        ) 
                    }
                }
                .addOnFailureListener { e ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "Erreur OCR: ${e.message}"
                        ) 
                    }
                }
        } catch (e: IOException) {
            _uiState.update { 
                it.copy(
                    isLoading = false,
                    error = "Erreur de chargement de l'image: ${e.message}"
                ) 
            }
        }
    }
}