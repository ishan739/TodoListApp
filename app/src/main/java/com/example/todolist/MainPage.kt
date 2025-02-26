package com.example.todolist


import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun MainPage(todoDao: TodoDao, modifier: Modifier) {
    val todoName = remember { mutableStateOf("") }
    val myContext = LocalContext.current
    val focusManager = LocalFocusManager.current
    val deleteDialogStatus = remember { mutableStateOf(false) }
    val deleteAllStatus = remember { mutableStateOf(false) }

    val clickedItemIndex = remember { mutableStateOf(0) }
    val updateDialogStatus = remember { mutableStateOf(false) }
    val clickedItem = remember { mutableStateOf("") }

    val textDialogStatus = remember { mutableStateOf(false) }

    val couroutineScope = rememberCoroutineScope()

    val todoList by todoDao.getAll().observeAsState(emptyList())

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .padding(top = 50.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(value = todoName.value,
                onValueChange = { todoName.value = it },
                label = { Text(text = "Enter Todo") },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    unfocusedLabelColor = Color.Gray,
                    focusedLabelColor = Color.White,
                    focusedContainerColor = colorResource(id = R.color.teal_700),
                    cursorColor = Color.White
                ),
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier
                    .border(
                        2.dp, Color.Black, RoundedCornerShape(5.dp)
                    )
                    .weight(7f)
                    .height(60.dp),
                textStyle = TextStyle(textAlign = TextAlign.Center)
            )
            Spacer(modifier = Modifier.width(5.dp))

            Button(
                onClick = {
                    if (todoName.value.isNotEmpty()) {
                        val newTodoContent = todoName.value
                        couroutineScope.launch(Dispatchers.IO) {
                            todoDao.insert(Todo(content = newTodoContent))
                        }
                        todoName.value = ""
                        focusManager.clearFocus()
                        Toast.makeText(myContext, "Added successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(myContext, "Please enter a todo", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .weight(3f)
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.teal_700),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(5.dp),
                border = BorderStroke(2.dp, Color.Black)
            ) {
                Text(text = "Add", fontSize = 20.sp)

            }

        }
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.padding(bottom = 80.dp, start = 5.dp, end = 5.dp)
            ) {

                items(count = todoList.size, itemContent = { index ->
                    val item = todoList.getOrNull(index)?.content ?: ""

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 1.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colorResource(id = R.color.green),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = item,
                                fontSize = 18.sp,
                                color = Color.White,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .width(300.dp)
                                    .weight(8F)
                                    .clickable {
                                        clickedItem.value = item
                                        textDialogStatus.value = true
                                    })

                            Row(
                                modifier = Modifier.weight(2F)
                            ) {
                                IconButton(
                                    onClick = {
                                        updateDialogStatus.value = true
                                        clickedItemIndex.value = index
                                        clickedItem.value = item
                                    }, modifier = Modifier.weight(1F)
                                ) {
                                    Icon(
                                        Icons.Filled.Edit,
                                        contentDescription = "Edit",
                                        tint = Color.White
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        deleteDialogStatus.value = true
                                        clickedItemIndex.value = index
                                    }, modifier = Modifier.weight(1F)

                                ) {
                                    Icon(
                                        Icons.Filled.Delete,
                                        contentDescription = "Delete",
                                        tint = Color.White
                                    )
                                }
                            }

                        }

                    }
                })


            }

            TextButton(
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red, contentColor = Color.White
                ),
                onClick = { deleteAllStatus.value = true },
                border = BorderStroke(2.dp, Color.Black),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Text(text = "Delete All")
            }
        }

        if (deleteAllStatus.value) {
            AlertDialog(onDismissRequest = { deleteAllStatus.value = false },
                title = {
                    Text(text = "Delete All")
                },
                text = { Text(text = "Are you sure you want to delete ALL the items?") },
                confirmButton = {
                    TextButton(onClick = {
                        couroutineScope.launch(Dispatchers.IO) {
                            todoDao.deleteAll()
                        }
                        deleteAllStatus.value = false
                    }) {
                        Text(text = "Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { deleteAllStatus.value = false }) {
                        Text(text = "No")
                    }
                })
        }


        if (deleteDialogStatus.value) {
            AlertDialog(onDismissRequest = { deleteDialogStatus.value = false },
                title = {
                    Text(text = "Delete")
                },
                text = { Text(text = "Are you sure you want to delete this item?") },
                confirmButton = {
                    TextButton(onClick = {
                        couroutineScope.launch(Dispatchers.IO) {
                            todoDao.delete(todoList[clickedItemIndex.value])
                        }
                        deleteDialogStatus.value = false
                    }) {
                        Text(text = "Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { deleteDialogStatus.value = false }) {
                        Text(text = "No")
                    }
                })
        }

        if (updateDialogStatus.value) {
            AlertDialog(onDismissRequest = { updateDialogStatus.value = false }, title = {
                Text(text = "Update")
            }, text = {
                TextField(value = clickedItem.value, onValueChange = {
                    clickedItem.value = it;
                })
            }, confirmButton = {
                TextButton(onClick = {
                    couroutineScope.launch(Dispatchers.IO) {
                        todoDao.update(todoList[clickedItemIndex.value].copy(content = clickedItem.value))
                    }
                    updateDialogStatus.value = false
                }) {
                    Text(text = "Yes")
                }
            }, dismissButton = {
                TextButton(onClick = { updateDialogStatus.value = false }) {
                    Text(text = "No")
                }
            })
        }

        if (textDialogStatus.value) {
            AlertDialog(
                onDismissRequest = { textDialogStatus.value = false },
                title = {
                    Text(text = "Todo Item")
                },
                text = {
                    Text(text = clickedItem.value)
                },
                confirmButton = {
                    TextButton(onClick = { textDialogStatus.value = false }) {
                        Text(text = "OK")
                    }
                },
            )
        }


    }

}
