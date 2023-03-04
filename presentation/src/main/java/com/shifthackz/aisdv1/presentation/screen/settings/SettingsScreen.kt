package com.shifthackz.aisdv1.presentation.screen.settings


/*
        val models: List<String>,
        val selectedModel: UiText = UiText.empty,
 */

//        var sdModelsExpanded by remember { mutableStateOf(false) }
//        ExposedDropdownMenuBox(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 8.dp),
//            expanded = sdModelsExpanded,
//            onExpandedChange = { sdModelsExpanded = !sdModelsExpanded },
//        ) {
//            val selectedModel = state.selectedModel.asString()
//            TextField(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .menuAnchor(),
//                value = selectedModel,
//                onValueChange = {},
//                readOnly = true,
//                label = { Text("SD Model") },
//                trailingIcon = {
//                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = sdModelsExpanded)
//                }
//            )
//
//            ExposedDropdownMenu(
//                expanded = sdModelsExpanded,
//                onDismissRequest = { sdModelsExpanded = false },
//            ) {
//                state.models.forEach { title ->
//                    DropdownMenuItem(
//                        text = { Text(title) },
//                        onClick = {
//                            sdModelsExpanded = false
//                            if (selectedModel == title) return@DropdownMenuItem
//                            onSelectedSdModel(title)
//                        },
//                    )
//                }
//            }
//        }