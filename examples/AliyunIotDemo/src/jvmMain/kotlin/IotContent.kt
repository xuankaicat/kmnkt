import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun IotContent(
    modifier: Modifier = Modifier,
    board: String,
    temperature: String,
    onTemperatureChanged: (String) -> Unit,
    humidity: String,
    onHumidityChanged: (String) -> Unit,
    brightness: String,
    onBrightnessChanged: (String) -> Unit,
    onConnectClicked: () -> Unit,
    onOpenClicked: () -> Unit,
    onCloseClicked: () -> Unit,
    onUploadClicked: () -> Unit,
    isSwitchOpen: Boolean,
    isConnected: Boolean,
) {
    Column{
        Title(modifier.weight(1f),
            isSwitchOpen = isSwitchOpen)

        Board(modifier.weight(3f),
            boardText = board)

        CommandButtons(modifier.weight(0.8f),
            onConnectClicked = onConnectClicked,
            onOpenClicked = onOpenClicked,
            onCloseClicked = onCloseClicked,
            isConnected = isConnected)

        val isTemperatureError = temperature.isNotEmpty()
                && temperature.toIntOrNull() !in -32768..32768

        val isHumidityError = humidity.isNotEmpty()
                && humidity.toIntOrNull() !in 0..65535

        val isBrightnessError = brightness.isNotEmpty()
                && brightness.toIntOrNull() !in 0..100

        TextFields(modifier.weight(3f),
            temperature = temperature,
            onTemperatureChanged = onTemperatureChanged,
            humidity = humidity,
            onHumidityChanged = onHumidityChanged,
            brightness = brightness,
            onBrightnessChanged = onBrightnessChanged,
            isTemperatureError = isTemperatureError,
            isHumidityError = isHumidityError,
            isBrightnessError = isBrightnessError,
            temperatureErrorTip = "温度只能是-32768~32768之间的整数值",
            humidityErrorTip = "湿度只能是0~65535之间的整数值",
            brightnessErrorTip = "亮度只能是0~100之间的整数值",
        )

        UploadButton(modifier.weight(0.8f),
            uploadAble = !(isTemperatureError || isHumidityError || isBrightnessError)
                    && (temperature.length + humidity.length + brightness.length) != 0,
            onUploadClicked = onUploadClicked)
    }
}

@Composable
private fun Title(modifier: Modifier, isSwitchOpen: Boolean) {
    Row(
        modifier
            .fillMaxWidth()
            .background(Color.LightGray)
    ) {
        Text("阿里云IOT虚拟设备控制",
            modifier.align(Alignment.CenterVertically)
                .padding(start = 8.dp)
        )
        Image(modifier = modifier
            .fillMaxWidth()
            .align(Alignment.CenterVertically)
            .padding(start = 48.dp),
            painter = painterResource(if(isSwitchOpen) "images/switch_on.svg" else "images/switch_off.svg"),
            contentDescription = "switch",
            contentScale = ContentScale.FillHeight
        )
    }
}

@Composable
private fun Board(
    modifier: Modifier,
    boardText: String
) {
    Box(
        modifier
            .padding(0.dp, 8.dp)
            .fillMaxWidth()
    ) {
        val stateVertical = rememberScrollState(0)
        Box(
            modifier
                .fillMaxSize()
                .verticalScroll(stateVertical)
        ) {
            Text(boardText)
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.BottomEnd)
                .fillMaxHeight(),
            adapter = rememberScrollbarAdapter(stateVertical)
        )
    }
}

@Composable
private fun CommandButtons(
    modifier: Modifier,
    onConnectClicked: () -> Unit,
    onOpenClicked: () -> Unit,
    onCloseClicked: () -> Unit,
    isConnected: Boolean,
) {
    Row(modifier
        .fillMaxWidth()
        .background(Color.LightGray)
    ) {
        val btnModifier = modifier
            .weight(1f)
            .fillMaxHeight()
            .padding(8.dp)
            .align(Alignment.CenterVertically)

        Button(modifier = btnModifier,
            onClick = onConnectClicked
        ) {
            Text(if(!isConnected) "连接" else "断开")
        }

        Button(modifier = btnModifier,
            onClick = onOpenClicked
        ) {
            Text("开")
        }

        Button(modifier = btnModifier,
            onClick = onCloseClicked
        ) {
            Text("关")
        }
    }
}

@Suppress("SameParameterValue")
@Composable
private fun TextFields(
    modifier: Modifier,
    temperature: String,
    onTemperatureChanged: (String) -> Unit,
    isTemperatureError: Boolean,
    temperatureErrorTip: String,
    humidity: String,
    onHumidityChanged: (String) -> Unit,
    isHumidityError: Boolean,
    humidityErrorTip: String,
    brightness: String,
    onBrightnessChanged: (String) -> Unit,
    isBrightnessError: Boolean,
    brightnessErrorTip: String,
) {
    Column(
        modifier
            .padding(bottom = 8.dp)
    ) {
        val textFieldModifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp, 0.dp)
            .height(60.dp)

        OutlinedTextField(modifier = textFieldModifier,
            value = temperature,
            onValueChange = onTemperatureChanged,
            label = { Text("温度" + if(isTemperatureError) "    参数不合法：$temperatureErrorTip" else "") },
            maxLines = 1,
            isError = isTemperatureError
        )

        OutlinedTextField(modifier = textFieldModifier,
            value = humidity,
            onValueChange = onHumidityChanged,
            label = { Text("湿度" + if(isHumidityError) "    参数不合法：$humidityErrorTip" else "") },
            maxLines = 1,
            isError = isHumidityError
        )

        OutlinedTextField(modifier = textFieldModifier,
            value = brightness,
            onValueChange = onBrightnessChanged,
            label = { Text("亮度" + if(isBrightnessError) "    参数不合法：$brightnessErrorTip" else "") },
            maxLines = 1,
            isError = isBrightnessError
        )
    }

}

@Composable
private fun UploadButton(
    modifier: Modifier,
    uploadAble: Boolean,
    onUploadClicked: () -> Unit,
) {
    Button(modifier = modifier
        .fillMaxWidth()
        .background(Color.LightGray)
        .padding(8.dp),
        enabled = uploadAble,
        onClick = onUploadClicked
    ) {
        Text("上传温湿亮度")
    }
}

@Composable
@Preview
fun IotContentPreview() {
    MaterialTheme {
        IotContent(
            board = "连接成功",
            temperature = "1234567890",
            onTemperatureChanged = {},
            humidity = "1234567890",
            onHumidityChanged = {},
            brightness = "1234567890",
            onBrightnessChanged = {},
            onConnectClicked = {},
            onOpenClicked = {},
            onCloseClicked = {},
            onUploadClicked = {},
            isSwitchOpen = true,
            isConnected = false
        )
    }
}