package com.example.airline.ui.scan

import android.content.Context
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.airline.utils.MrzData
import com.example.airline.utils.MrzParser
import com.example.airline.utils.ImageUtils
import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class PassportScanUiState(
    val isCameraReady: Boolean = false,
    val isProcessingOcr: Boolean = false,
    val showCapturedImage: Boolean = false,
    val imageCaptureUri: String? = null,
    val extractedMrzData: MrzData? = null,
    val error: String? = null,
    val isLoading: Boolean = false
)

class PassportScanViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PassportScanUiState())
    val uiState: StateFlow<PassportScanUiState> = _uiState

    // CameraX ImageCapture holder for UI binding
    var imageCapture: ImageCapture? = null

    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    fun setCameraReady() {
        _uiState.value = _uiState.value.copy(isCameraReady = true)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun retakePhoto() {
        _uiState.value = _uiState.value.copy(showCapturedImage = false, imageCaptureUri = null, error = null)
    }

    fun confirmPhoto(context: Context) {
        processMrzOcr(context)
    }

    fun processMrzOcr(context: Context) {
        _uiState.value = _uiState.value.copy(isLoading = true, isProcessingOcr = true, error = null)
        val capture = imageCapture
        if (capture == null) {
            _uiState.value = _uiState.value.copy(
                error = "Caméra indisponible",
                isLoading = false,
                isProcessingOcr = false
            )
            return
        }

        capture.takePicture(ContextCompat.getMainExecutor(context), object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                scanImageProxy(image)
            }

            override fun onError(exception: ImageCaptureException) {
                _uiState.value = _uiState.value.copy(
                    error = "Erreur de capture : ${exception.message}",
                    isLoading = false,
                    isProcessingOcr = false
                )
            }
        })
    }

    private fun scanImageProxy(imageProxy: ImageProxy) {
        imageProxy.image?.let { mediaImage ->
            val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            processTextImage(inputImage, imageProxy)
            return
        }

        val bitmap = ImageUtils.imageProxyToBitmap(imageProxy)
        if (bitmap == null) {
            imageProxy.close()
            _uiState.value = _uiState.value.copy(
                error = "Impossible de lire l'image capturée",
                isLoading = false,
                isProcessingOcr = false
            )
            return
        }

        val variants = mutableListOf<Bitmap>()
        variants.add(bitmap)
        variants.add(ImageUtils.toGrayscale(bitmap))
        variants.add(ImageUtils.adjustContrastBrightness(ImageUtils.toGrayscale(bitmap), 1.4f, 0f))
        variants.add(ImageUtils.rotate(ImageUtils.toGrayscale(bitmap), 90f))
        variants.add(ImageUtils.rotate(ImageUtils.toGrayscale(bitmap), 270f))

        fun tryVariant(index: Int) {
            if (index >= variants.size) {
                imageProxy.close()
                _uiState.value = _uiState.value.copy(
                    error = "MRZ non détecté. Essayez un autre alignement.",
                    isLoading = false,
                    isProcessingOcr = false
                )
                return
            }

            val img = variants[index]
            val inputImage = InputImage.fromBitmap(img, 0)
            textRecognizer.process(inputImage)
                .addOnSuccessListener { visionText ->
                    val (mrz, valid) = MrzParser.extractFromRawTextWithValidation(visionText.text)
                    if (mrz != null) {
                        imageProxy.close()
                        _uiState.value = _uiState.value.copy(
                            extractedMrzData = mrz,
                            isLoading = false,
                            isProcessingOcr = false
                        )
                    } else {
                        tryVariant(index + 1)
                    }
                }
                .addOnFailureListener {
                    tryVariant(index + 1)
                }
        }

        tryVariant(0)
    }

    private fun processTextImage(inputImage: InputImage, imageProxy: ImageProxy) {
        textRecognizer.process(inputImage)
            .addOnSuccessListener { visionText ->
                val (mrz, valid) = MrzParser.extractFromRawTextWithValidation(visionText.text)
                if (mrz != null) {
                    imageProxy.close()
                    _uiState.value = _uiState.value.copy(
                        extractedMrzData = mrz,
                        isLoading = false,
                        isProcessingOcr = false
                    )
                } else {
                    val bitmap = ImageUtils.imageProxyToBitmap(imageProxy)
                    if (bitmap == null) {
                        imageProxy.close()
                        _uiState.value = _uiState.value.copy(
                            error = "Impossible de lire l'image capturée",
                            isLoading = false,
                            isProcessingOcr = false
                        )
                        return@addOnSuccessListener
                    }

                    val variants = mutableListOf<Bitmap>()
                    variants.add(ImageUtils.toGrayscale(bitmap))
                    variants.add(ImageUtils.adjustContrastBrightness(ImageUtils.toGrayscale(bitmap), 1.4f, 0f))
                    variants.add(ImageUtils.rotate(ImageUtils.toGrayscale(bitmap), 90f))
                    variants.add(ImageUtils.rotate(ImageUtils.toGrayscale(bitmap), 270f))

                    fun tryVariant(index: Int) {
                        if (index >= variants.size) {
                            imageProxy.close()
                            _uiState.value = _uiState.value.copy(
                                error = "MRZ non détecté. Essayez un autre alignement.",
                                isLoading = false,
                                isProcessingOcr = false
                            )
                            return
                        }

                        val img = variants[index]
                        val variantImage = InputImage.fromBitmap(img, 0)
                        textRecognizer.process(variantImage)
                            .addOnSuccessListener { variantText ->
                                val (variantMrz, _) = MrzParser.extractFromRawTextWithValidation(variantText.text)
                                if (variantMrz != null) {
                                    imageProxy.close()
                                    _uiState.value = _uiState.value.copy(
                                        extractedMrzData = variantMrz,
                                        isLoading = false,
                                        isProcessingOcr = false
                                    )
                                } else {
                                    tryVariant(index + 1)
                                }
                            }
                            .addOnFailureListener {
                                tryVariant(index + 1)
                            }
                    }

                    tryVariant(0)
                }
            }
            .addOnFailureListener { exception ->
                imageProxy.close()
                _uiState.value = _uiState.value.copy(
                    error = "Lecture OCR impossible : ${exception.message}",
                    isLoading = false,
                    isProcessingOcr = false
                )
            }
    }

    override fun onCleared() {
        super.onCleared()
        textRecognizer.close()
    }
}
