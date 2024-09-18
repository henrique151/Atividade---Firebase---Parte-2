package com.example.aulapamfirebase

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.aulapamfirebase.ui.theme.AulaPAMFireBaseTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {

    private val db by lazy { Firebase.firestore }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            AulaPAMFireBaseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App(db)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(db: FirebaseFirestore) {
    var nome by remember { mutableStateOf("") }
    var matricula by remember { mutableStateOf("") }
    var turma by remember { mutableStateOf("") }
    var cpf by remember { mutableStateOf("") }
    var rg by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var dataNascimento by remember { mutableStateOf("") }
    var sexo by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var dialogTitle by remember { mutableStateOf("") }

    fun showDialogMessage(message: String, title: String) {
        dialogMessage = message
        dialogTitle = title
        showDialog = true
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = dialogTitle) },
            text = { Text(text = dialogMessage) },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    fun applyMask(value: String, mask: String): String {
        val rawValue = value.replace("[^\\d]".toRegex(), "")
        val maskedValue = StringBuilder()
        var i = 0
        for (char in mask) {
            if (char == '#') {
                if (i < rawValue.length) {
                    maskedValue.append(rawValue[i])
                    i++
                }
            } else {
                maskedValue.append(char)
            }
        }
        return maskedValue.toString()
    }

    fun removeMask(value: String): String {
        return value.replace("[^\\d]".toRegex(), "")
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Atividade - App Firebase",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Henrique Porto de Sousa 3°DS Manhã",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                @Composable
                fun TextFieldWithLabel(
                    label: String,
                    value: String,
                    onValueChange: (String) -> Unit,
                    isNumber: Boolean = false,
                    mask: String? = null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$label:",
                            modifier = Modifier.width(100.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        val transformedValue = if (isNumber && mask != null) {
                            applyMask(value, mask)
                        } else {
                            value
                        }
                        TextField(
                            value = transformedValue,
                            onValueChange = {
                                val cleanedValue = if (isNumber && mask != null) {
                                    removeMask(it)
                                } else {
                                    it
                                }
                                onValueChange(cleanedValue)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .border(1.dp, Color(0xFFADD8E6), RoundedCornerShape(8.dp)),
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color.White,
                                focusedIndicatorColor = Color(0xFFADD8E6),
                                unfocusedIndicatorColor = Color.LightGray
                            ),
                            textStyle = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = if (isNumber) KeyboardType.Number else KeyboardType.Text,
                                imeAction = ImeAction.Next
                            )
                        )
                    }
                }

                TextFieldWithLabel("Nome", nome, { nome = it })
                TextFieldWithLabel("Matricula", matricula, { matricula = it })
                TextFieldWithLabel("Turma", turma, { turma = it })
                TextFieldWithLabel("CPF", cpf, { cpf = it }, isNumber = true, mask = "###.###.###-##")
                TextFieldWithLabel("RG", rg, { rg = it }, isNumber = true, mask = "##.###.###-#")
                TextFieldWithLabel("Telefone", telefone, { telefone = it }, isNumber = true, mask = "(##) #####-####")
                TextFieldWithLabel("Data Nasc.", dataNascimento, { dataNascimento = it }, isNumber = true, mask = "##/##/####")
                TextFieldWithLabel("Sexo", sexo, { sexo = it })

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        val studentData = hashMapOf(
                            "nome" to nome,
                            "matricula" to matricula,
                            "turma" to turma,
                            "cpf" to cpf,
                            "rg" to rg,
                            "telefone" to telefone,
                            "dataNascimento" to dataNascimento,
                            "sexo" to sexo
                        )
                        db.collection("Escola").document("Aluno")
                            .set(studentData)
                            .addOnSuccessListener {
                                Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!")
                                showDialogMessage("Cadastro realizado com sucesso!", "Sucesso")
                            }
                            .addOnFailureListener { e ->
                                Log.w(ContentValues.TAG, "Error writing document", e)
                                showDialogMessage("Erro ao realizar o cadastro.", "Erro")
                            }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF003366)
                    )
                ) {
                    Text(text = "Cadastrar", color = Color.White)
                }
            }
        }
    }
}
