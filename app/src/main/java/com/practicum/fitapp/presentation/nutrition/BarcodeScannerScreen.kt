package com.practicum.fitapp.presentation.nutrition

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border // <-- Исправит ошибку 'border'
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.practicum.fitapp.ui.theme.PrimaryGreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.util.concurrent.Executors

data class ScannedProduct(
    val name: String,
    val calories: Int,
    val protein: Double,
    val fat: Double,
    val carbs: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarcodeScannerScreen(
    onBack: () -> Unit,
    onProductFound: (ScannedProduct) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasCameraPermission by remember { mutableStateOf(false) }
    var isScanning by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted -> hasCameraPermission = isGranted }

    LaunchedEffect(Unit) {
        val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionCheck == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            hasCameraPermission = true
        } else {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (hasCameraPermission) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }

                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()

                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()

                        imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                            if (!isScanning) {
                                imageProxy.close()
                                return@setAnalyzer
                            }

                            val mediaImage = imageProxy.image
                            if (mediaImage != null) {
                                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                                val scanner = BarcodeScanning.getClient()

                                scanner.process(image)
                                    .addOnSuccessListener { barcodes ->
                                        for (barcode in barcodes) {
                                            barcode.rawValue?.let { barcodeValue ->
                                                isScanning = false
                                                Log.d("BarcodeScanner", "Найден штрихкод: $barcodeValue")

                                                fetchProductFromApi(barcodeValue) { product ->
                                                    if (product != null) {
                                                        onProductFound(product)
                                                    } else {
                                                        errorMessage = "Продукт не найден в базе"
                                                        isScanning = true
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    .addOnFailureListener { Log.e("BarcodeScanner", "Ошибка", it) }
                                    .addOnCompleteListener { imageProxy.close() }
                            } else {
                                imageProxy.close()
                            }
                        }

                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis)
                        } catch (e: Exception) {
                            Log.e("BarcodeScanner", "Ошибка камеры", e)
                        }
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(250.dp)
                    .border(2.dp, PrimaryGreen, RoundedCornerShape(16.dp))
            )

            Column(
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isScanning) {
                    Text("Наведите камеру на штрихкод", color = Color.White, fontSize = 16.sp)
                } else {
                    CircularProgressIndicator(color = PrimaryGreen)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Ищем продукт...", color = Color.White, fontSize = 16.sp)
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Нет доступа к камере", color = Color.White, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { launcher.launch(Manifest.permission.CAMERA) }) {
                    Text("Разрешить")
                }
            }
        }

        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = Color.White)
        }

        if (errorMessage != null) {
            Card(
                modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.8f))
            ) {
                Text(
                    text = errorMessage!!,
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

// Вспомогательная функция вынесена на уровень файла (вне Composable)
private fun fetchProductFromApi(barcode: String, onResult: (ScannedProduct?) -> Unit) {
    Thread {
        try {
            val url = URL("https://world.openfoodfacts.org/api/v2/product/$barcode.json")
            val connection = url.openConnection()
            connection.setRequestProperty("User-Agent", "FitApp/1.0")

            val response = connection.getInputStream().bufferedReader().use { it.readText() }
            val json = JSONObject(response)

            if (json.optInt("status") == 1) {
                val productObj = json.getJSONObject("product")
                val name = productObj.optString("product_name", "Неизвестный продукт")
                val nutriments = productObj.optJSONObject("nutriments")

                val calories = nutriments?.optDouble("energy-kcal_100g", 0.0)?.toInt() ?: 0
                val protein = nutriments?.optDouble("proteins_100g", 0.0) ?: 0.0
                val fat = nutriments?.optDouble("fat_100g", 0.0) ?: 0.0
                val carbs = nutriments?.optDouble("carbohydrates_100g", 0.0) ?: 0.0

                val result = ScannedProduct(name, calories, protein, fat, carbs)
                android.os.Handler(android.os.Looper.getMainLooper()).post { onResult(result) }
            } else {
                android.os.Handler(android.os.Looper.getMainLooper()).post { onResult(null) }
            }
        } catch (e: Exception) {
            Log.e("BarcodeScanner", "Ошибка API", e)
            android.os.Handler(android.os.Looper.getMainLooper()).post { onResult(null) }
        }
    }.start()
}