package com.feng.insurance.screen

import android.app.Application
import android.content.ContentValues.TAG
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.ImageFormat.JPEG
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64

import android.util.Log
import android.view.Gravity.apply
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.key.Key.Companion.I
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toFile
import androidx.core.view.GravityCompat.apply
import androidx.navigation.NavHostController
import com.feng.insurance.Routes
import com.feng.insurance.model.LoginViewModel
import com.feng.insurance.service.*
import com.feng.insurance.ui.components.Chip
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.internal.Internal.instance
import java.io.*
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun InsuranceDetails(navController: NavHostController, viewModel: LoginViewModel, insuranceID: Int?) {
    val modalBottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    val coroutineScope = rememberCoroutineScope()

    val insurance = remember {
        derivedStateOf {
            viewModel.insuranceList.find {
                it.id == insuranceID
            }
        }
    }

    val events = remember {
        derivedStateOf {
            viewModel.insuranceList.find {
                it.id == insuranceID
            }
        }
    }

    ModalBottomSheetLayout(
        sheetState = modalBottomSheetState,
        sheetContent = {
            CreateEventDialog(viewModel, modalBottomSheetState, insuranceID)
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Insure/Events") },
                    actions = {
                        TextButton(onClick = {
                            coroutineScope.launch {
                                modalBottomSheetState.show()
                            }
                        }) {
                            Text(text = "Report new event", color = Color.White)
                        }
                        TextButton(onClick = { viewModel.logout() }) {
                            Text(text = "Logout", color = Color.White)
                        }
                    }
                )
            }
        ) {
            Column() {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Column() {
                        Text(text = insurance.value?.title ?: "")
                        Text(text = insurance.value?.address ?: "")
                        Text(text = insurance.value?.category ?: "")
                        Text(text = insurance.value?.status.toString() ?: "")
                    }
                }
                LazyColumn {
                    items(insurance.value?.events?: listOf()) { e ->
                        EventListItem(e)
                    }
                }
            }
        }
    }

}

@Composable
fun EventListItem(event: Event) {
    Column {
        Text(text = event.title)
        Text(text = event.description)
        Text(text = event.payoutRange)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(24.dp)) {
            event.damageType?.forEach {
                Chip(it.toString())
            }
        }
//        Paveiksleliai
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf(1, 2, 3).forEach {
                Box(modifier = Modifier
                    .size(100.dp)
                    .background(Color.Gray))
            }
        }
    }
}

private data class Estimate(
    var min: Int,
    var max: Int,
)

@Composable
fun DamageItem(
    label: String,
    enabled: Boolean,
    onEnabledChange: ((Boolean) -> Unit)?,
    valueLabel: String?,
    value: Int?,
    onValueChange: ((Int) -> Unit)?,
) {
    Column {
        Row {
            Text(text = label)
            Switch(modifier = Modifier.fillMaxWidth(), checked = enabled, onCheckedChange = onEnabledChange)
        }

        if (enabled && valueLabel != null) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = valueLabel ?: "") },
                value = "$value",
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                onValueChange = {
                    val parsedValue = if (it.isEmpty()) 0 else it.toInt(10)
                    if (onValueChange != null) {
                        onValueChange(parsedValue)
                    }
                })
        }
    }
}

fun ClosedRange<Char>.randomString(length: Int) =
    (1..length)
        .map { (Random().nextInt(endInclusive.toInt() - start.toInt()) + start.toInt()).toChar() }
        .joinToString("")

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CreateEventDialog(viewModel: LoginViewModel, sheetState: ModalBottomSheetState, insuranceID: Int?) {
    val coroutineScope = rememberCoroutineScope()
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageUri2 = remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    var byteArray = remember { ByteArray(0) }
    var filename =remember{ String()}
    val bitmap2 = remember { mutableStateOf<Bitmap?>(null) }
    val result = remember {
        mutableStateOf<Bitmap?>(null)
    }
//    val file = File.createTempFile("","")
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
        }


    val bitmap = remember(imageUri) {
        derivedStateOf {
            imageUri?.let{
                val source = ImageDecoder.createSource(context.contentResolver, it)
                ImageDecoder.decodeBitmap(source)
            }
        }
    }

    val bitmapImage = remember(bitmap) {
        derivedStateOf {
            bitmap.value?.let{ it.asImageBitmap() }
        }
    }



//    val fileuri= String
//    val fos :FileOutputStream
//    var newfile : File
//    val byteArray = remember { mutableStateOf(0) }
//    newfile = File(imageUri.toString())
//    var requestfile = RequestBody.create(MediaType.parse("multipart/form-data"), newfile)
    val title = remember { mutableStateOf(TextFieldValue()) }
    val description = remember { mutableStateOf(TextFieldValue()) }

    val fireDamageEnabled = remember { mutableStateOf(false) }
    val fireDamageSize = remember { mutableStateOf(0) }

    val smalleholeceilingEnabled = remember { mutableStateOf(false) }
    val smalleholeceilingSize = remember { mutableStateOf(0) }

    val minotwaterDamageEnabled = remember { mutableStateOf(false) }
    val minorwaterDamageSize = remember { mutableStateOf(0) }

    val brokenWindowsEnabled = remember { mutableStateOf(false) }
    val brokenWindowCount = remember { mutableStateOf(0) }

    val mediumtolargeholeceilingEnabled = remember {mutableStateOf(false)}
    val mediumtolargeholeceilingSize = remember {mutableStateOf(0)}

    val structuraldamageceilingEnabled = remember {mutableStateOf(false)}
    val structuraldamageceilingSize = remember {mutableStateOf(1)}
//
    val waterdamagefloorEnabled = remember {mutableStateOf(false)}
    val waterdamagefloorSize = remember {mutableStateOf(0)}

    val estimate = remember {
        derivedStateOf {
            val fireSize = if (fireDamageEnabled.value) fireDamageSize.value else 0
            val minorwaterSize = if (minotwaterDamageEnabled.value) minorwaterDamageSize.value else 0
            val windowCount = if (brokenWindowsEnabled.value) brokenWindowCount.value else 0
            val cielinghole = if(smalleholeceilingEnabled.value) smalleholeceilingSize.value else 0
            val mediumcielinghole = if(mediumtolargeholeceilingEnabled.value) mediumtolargeholeceilingSize.value else 0
            val structuraldamage = if(structuraldamageceilingEnabled.value) structuraldamageceilingSize.value else 0
            val min = minorwaterSize * 200 + windowCount * 200 + fireSize * 10 + cielinghole * 50 + mediumcielinghole * 200 + structuraldamage * 2000
            val max = minorwaterSize * 500 + windowCount * 600 + fireSize * 100 + cielinghole * 150 + mediumcielinghole * 400 + structuraldamage * 8000
            Estimate(min = min, max = max)
        }
    }

    val isCreateButtonDisabled = remember {
        derivedStateOf {
            title.value.text.isEmpty() || description.value.text.isEmpty()
        }
    }

    fun closeSheet() {
        coroutineScope.launch {
            sheetState.hide()
        }
    }

//    fun bitmapToMultipart(imageBitmap: Bitmap): MultipartBody.Part {
//        val bos = ByteArrayOutputStream()
//        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 80 /*ignored for PNG*/, bos)
//        val bitmapdata = bos.toByteArray()
//        Log.i(TAG, "bitmapToMultipart: ${Base64.encodeToString(bitmapdata,Base64.NO_WRAP)}")
//
//        val name :RequestBody = bitmapdata
//        return MultipartBody.Part.createFormData("file", "avatar", name)
//    }


    fun convertBitmapToFile(fileName: String, bitmap: Bitmap): File {
        //create a file to write bitmap data
        val file = File(context.cacheDir, fileName)
        file.createNewFile()

        //Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0 /*ignored for PNG*/, bos)
        val bitMapData = bos.toByteArray()

        //write the bytes in file
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        try {
            fos?.write(bitMapData)
            fos?.flush()
            fos?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }

    fun buildImageBodyPart( bitmap: Bitmap?): MultipartBody.Part {
        if (bitmap == null) {
            return MultipartBody.Part.createFormData("", "")
        }
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,80,bos)
        val bitmapdata = bos.toByteArray()

        val reqFile = RequestBody.create(MediaType.parse("image/*"), bitmapdata)
        return MultipartBody.Part.createFormData("file", "image.jpg", reqFile)
    }

    fun createNewInsurance() {
        if (insuranceID == null) {
            throw Exception("Why is this null?")
        }

        closeSheet()

        var damageType: MutableList<DamageType> = arrayListOf()
//        if (waterDamageEnabled.value) {
//            damageType.add(DamageType.WATER_DAMAGE)
//        }

        if (brokenWindowsEnabled.value) {
            damageType.add(DamageType.BROKEN_WINDOWS)
        }
        if(smalleholeceilingEnabled.value){
            damageType.add(DamageType.SMALL_HOLE_CEILING)
        }

        val byteArray = null
        val file = null
        viewModel.createNewEvent(
            damageType = damageType ,
            description = description.value.text,
            insuranceId = insuranceID,
            payoutRange = "${estimate.value.min}-${estimate.value.max}",
            title = title.value.text,
            file = buildImageBodyPart(bitmap.value)
        )
    }

    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Create new insurance", fontSize = 24.sp)
        Text(text = "Estimated payment: ${estimate.value.min}-${estimate.value.max}")


        bitmapImage.value?.let{
            Image(bitmap = it ,
                contentDescription = null,
                modifier = Modifier
                    .size(400.dp)
                    .padding(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = { launcher.launch("image/*") }) {
            Text(text = "Pick Image")
        }
        TextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Title") },
            value = title.value,
            onValueChange = { title.value = it }
        )
        TextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Description") },
            value = description.value,
            onValueChange = { description.value = it }
        )
//        ActivityResultContracts.TakePicture(bitmap)

        DamageItem(
            label = "fire",
            enabled = fireDamageEnabled.value,
            onEnabledChange = { fireDamageEnabled.value = it },
            valueLabel = "square feet",
            value = fireDamageSize.value,
            onValueChange = { fireDamageSize.value = it }
        )
        DamageItem(
            label="small hole ceiling",
            enabled = smalleholeceilingEnabled.value,
            onEnabledChange = { smalleholeceilingEnabled.value = it },
            valueLabel = "square feet",
            value = smalleholeceilingSize.value,
            onValueChange = { smalleholeceilingSize.value = it }
        )
        DamageItem(
            label="Medium to large hole ceiling",
            enabled = mediumtolargeholeceilingEnabled.value,
            onEnabledChange = { mediumtolargeholeceilingEnabled.value = it },
            valueLabel = "square feet",
            value = mediumtolargeholeceilingSize.value,
            onValueChange = { mediumtolargeholeceilingSize.value = it }
        )
        Row{
            Text(text="Structural ceiling damage")
            Switch(modifier = Modifier.fillMaxWidth(), checked = structuraldamageceilingEnabled.value, onCheckedChange = { structuraldamageceilingEnabled.value = it })
        }


        Row {
            Text(text = "Water damage")
            Switch(modifier = Modifier.fillMaxWidth(), checked = minotwaterDamageEnabled.value, onCheckedChange = { minotwaterDamageEnabled.value = it })
        }

        if (minotwaterDamageEnabled.value) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Damages in square feet") },
                value = "${minorwaterDamageSize.value}",
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                onValueChange = { minorwaterDamageSize.value = if (it.isEmpty()) 0 else it.toInt() })
        }

        Row {
            Text(text = "Broken windows")
            Switch(modifier = Modifier.fillMaxWidth(), checked = brokenWindowsEnabled.value, onCheckedChange = { brokenWindowsEnabled.value = it })
        }


        if (brokenWindowsEnabled.value) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Broken window count") },
                value = "${brokenWindowCount.value}",
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                onValueChange = { brokenWindowCount.value = if (it.isEmpty()) 0 else it.toInt(10) })
        }
//        Row {
//            Text(text = "Hole in ceiling")
//            Switch(modifier = Modifier.fillMaxWidth(), checked = brokenWindowsEnabled.value, onCheckedChange = { brokenWindowsEnabled.value = it })
//        }

        Row(
            modifier = Modifier.padding(all = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = { closeSheet() },
            ) {
                Text("Cancel")
            }
            TextButton(
                onClick = { createNewInsurance() },
                enabled = !isCreateButtonDisabled.value
            ) {
                Text("Create")
            }
        }

    }



}
