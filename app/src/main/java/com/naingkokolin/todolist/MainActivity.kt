package com.naingkokolin.todolist

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naingkokolin.todolist.ui.theme.ToDoListTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToDoListTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    ToDoListApp()
                }
            }
        }
    }
}

@Composable
fun ToDoListItem(
    list: ToDoList,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isChecked by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .size(width = 400.dp, height = 100.dp)
            .padding(16.dp)
            .border(1.dp, Color.Black, RectangleShape),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = list.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = list.description,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 4,
                textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(10.dp)
        ) {
            IconButton(onClick = {
                // Edit the todolist title and description
                onEditClick()
            }) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Icon")
            }
            Checkbox(
                checked = isChecked,
                onCheckedChange = { isChecked = !isChecked },
                colors = CheckboxDefaults.colors(
                    uncheckedColor = Color.Gray,
                    checkedColor = Color.White,
                    checkmarkColor = Color.Black
                ),
                modifier = Modifier
                    .border(1.2.dp, Color.Black, RoundedCornerShape(20))
                    .size(36.dp)
            )
        }
    }
}

// Main Function
@Composable
fun ToDoListApp(modifier: Modifier = Modifier) {

    var todoList by remember { mutableStateOf(listOf<ToDoList>()) }
    var showDialog by remember { mutableStateOf(false) }
    var listTitle by remember { mutableStateOf("") }
    var listDescription by remember { mutableStateOf("") }

    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(13f)
                .padding(top = 24.dp)
        ) {
            items(todoList) { list ->
                if (list.isEditing) {
                    ListEditor(list = list, onEditComplete = {
                        editedTitle, editedDescription ->
                        todoList = todoList.map { it.copy(isEditing = false) }
                        val editedList = todoList.find { it.id == list.id }
                        editedList?.let {
                            it.title = editedTitle
                            it.description = editedDescription
                        }
                    })
                } else {
                    ToDoListItem(
                        list = list,
                        onEditClick = {
                            // finding out which list we are editing and changing is "isEditing boolean" to true
                            todoList = todoList.map { it.copy(isEditing = it.id == list.id) }
                        }
                    )
                }
            }
        }

        // Add Item button
        Button(
            onClick = { showDialog = true },
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(30)),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White,
                containerColor = Color.Black
            )
        ) {
            Text(text = "Add Item")
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Box {
                    Column(
                        modifier = Modifier
                            .wrapContentHeight()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // For title
                        OutlinedTextField(
                            value = listTitle,
                            onValueChange = { listTitle = it },
                            label = { Text(text = "Title")},
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Next,
                                keyboardType = KeyboardType.Text
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // For description
                        OutlinedTextField(
                            value = listDescription,
                            onValueChange = { listDescription = it },
                            label = { Text(text = "Description")},
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = false,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done,
                                keyboardType = KeyboardType.Text
                            )
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // cancel btn
                            Button(onClick = {
                                // clear title and description text fields and close alert box
                                showDialog = false
                                listTitle = ""
                                listDescription = ""
                            }) {
                                Text("Cancel")
                            }

                            // add btn
                            Button(onClick = {
                                // add title and description in the todoList
                                if (listTitle.isNotBlank() && listDescription.isNotBlank()) {
                                    val newList = ToDoList(
                                        id = todoList.size + 1,
                                        title = listTitle,
                                        description = listDescription
                                    )
                                    todoList += newList
                                    showDialog = false
                                    listTitle = ""
                                    listDescription = ""
                                } else {
                                    when {
                                        listTitle.isBlank() -> Toast.makeText(
                                            context,
                                            "Enter title",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        else -> Toast.makeText(
                                            context,
                                            "Enter description",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }) {
                                Text("Add List")
                            }
                        }
                    }
                }
            }
        )
    }
}

// updating or editing data
@Composable
fun ListEditor(
    list: ToDoList,
    onEditComplete: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {

    var editedTitle by remember { mutableStateOf(list.title) }
    var editedDescription by remember { mutableStateOf(list.description) }
    var isEditing by remember { mutableStateOf(list.isEditing) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .padding(8.dp)
            .width(300.dp)
            .wrapContentHeight()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(10),
                ambientColor = Color.Red,
            )
    ) {
        // for title
        BasicTextField(
            value = editedTitle,
            onValueChange = { editedTitle = it },
            textStyle = TextStyle.Default.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                .fillMaxWidth()
        )

        // for description
        BasicTextField(
            value = editedDescription,
            onValueChange = { editedDescription = it },
            textStyle = TextStyle.Default.copy(
                fontWeight = FontWeight.W300,
                fontSize = 14.sp
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp)
        )

        Button(
            onClick = {
                isEditing = false
                onEditComplete(editedTitle, editedDescription)
            },
            modifier = Modifier.padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White,
                containerColor = Color.DarkGray
            )
        ) {
            Text("Save", modifier = Modifier.padding(4.dp))
            Icon(imageVector = Icons.Default.Done, contentDescription = "Save Button")
        }
    }
}

// TodoList Data class
data class ToDoList(
    val id: Int,
    var title: String,
    var description: String,
    var isEditing: Boolean = false
)

@Preview(showBackground = true)
@Composable
private fun ToDoListAppPreview() {
//    ToDoListItem(list = ToDoList(1, "Title", "Description"), { })
//    ToDoListApp()
    ListEditor(
        list = ToDoList(1, "Title", "Description"),
        onEditComplete = { _, _-> })
}