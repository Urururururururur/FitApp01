package com.practicum.fitapp.presentation.nutrition

val builtInRecipes: List<Recipe> = listOf(
    Recipe(
        id = "breakfast-1",
        title = "Овсянка с творогом и яблоком",
        mealType = "breakfast",
        description = "Сытный завтрак без сахара: овсянка, нежирный творог и яблоко.",
        cookingTimeMinutes = 15,
        ingredients = listOf(
            RecipeIngredient("Овсянка (крупа)", 60.0, 207.0, 7.1, 3.5, 39.2),
            RecipeIngredient("Творог нежирный", 150.0, 132.0, 27.0, 0.9, 2.9),
            RecipeIngredient("Яблоки", 100.0, 46.0, 0.4, 0.0, 11.3),
            RecipeIngredient("Вода", 200.0, 0.0, 0.0, 0.0, 0.0)
        ),
        totalWeightGrams = 510.0,
        totalsKcal = 385.0, totalsProtein = 34.5, totalsFat = 4.4, totalsCarbs = 53.4,
        steps = listOf("Отварить овсянку в воде до готовности.", "Смешать готовую овсянку с нежирным творогом.", "Добавить нарезанное яблоко и перемешать.")
    ),
    Recipe(
        id = "breakfast-2",
        title = "Омлет с индейкой и овощами",
        mealType = "breakfast",
        description = "Белковый завтрак: омлет с индейкой, помидорами и сладким перцем.",
        cookingTimeMinutes = 20,
        ingredients = listOf(
            RecipeIngredient("Яйцо куриное (целое)", 100.0, 157.0, 12.7, 11.5, 0.7),
            RecipeIngredient("Индейка", 100.0, 210.0, 20.0, 13.0, 0.0),
            RecipeIngredient("Помидоры", 100.0, 20.0, 1.1, 0.2, 3.8),
            RecipeIngredient("Перец сладкий", 50.0, 13.5, 0.7, 0.0, 2.7),
            RecipeIngredient("Масло оливковое", 5.0, 44.9, 0.0, 5.0, 0.0)
        ),
        totalWeightGrams = 386.5,
        totalsKcal = 445.4, totalsProtein = 34.5, totalsFat = 29.7, totalsCarbs = 7.2,
        steps = listOf("Нарезать индейку и овощи.", "Слегка обжарить индейку на оливковом масле, добавить овощи и воду.", "Влить взбитые яйца, посолить, поперчить и готовить под крышкой до готовности.")
    ),
    Recipe(
        id = "breakfast-3",
        title = "Творожная запеканка с грушей",
        mealType = "breakfast",
        description = "Нежная запеканка из полужирного творога, овсянки и груши.",
        cookingTimeMinutes = 35,
        ingredients = listOf(
            RecipeIngredient("Творог полужирный (9%)", 200.0, 318.0, 33.4, 18.0, 4.0),
            RecipeIngredient("Яйцо куриное (целое)", 50.0, 78.5, 6.4, 5.8, 0.4),
            RecipeIngredient("Овсянка (крупа)", 30.0, 103.5, 3.6, 1.7, 19.6),
            RecipeIngredient("Груши", 100.0, 42.0, 0.4, 0.0, 10.7)
        ),
        totalWeightGrams = 431.0,
        totalsKcal = 542.0, totalsProtein = 43.8, totalsFat = 25.5, totalsCarbs = 34.7,
        steps = listOf("Смешать творог, яйцо, овсянку, воду и соду.", "Добавить нарезанную грушу и перемешать.", "Выложить массу в форму и запекать при 180 °C около 25 минут.")
    ),
    Recipe(
        id = "lunch-1",
        title = "Гречка с куриной грудкой и овощами",
        mealType = "lunch",
        description = "Классическое ПП-блюдо: гречка, куриная грудка, морковь и лук.",
        cookingTimeMinutes = 35,
        ingredients = listOf(
            RecipeIngredient("Куриная грудка (филе)", 150.0, 169.5, 35.4, 2.9, 0.6),
            RecipeIngredient("Гречка (крупа)", 60.0, 197.4, 7.6, 1.6, 40.8),
            RecipeIngredient("Морковь", 50.0, 16.5, 0.7, 0.1, 3.5),
            RecipeIngredient("Лук репчатый", 30.0, 12.3, 0.4, 0.1, 2.5),
            RecipeIngredient("Масло оливковое", 5.0, 44.9, 0.0, 5.0, 0.0)
        ),
        totalWeightGrams = 496.0,
        totalsKcal = 440.6, totalsProtein = 44.1, totalsFat = 9.7, totalsCarbs = 47.4,
        steps = listOf("Отварить гречку в воде до готовности.", "Нарезать куриную грудку и овощи.", "Тушить курицу с морковью и луком на оливковом масле, добавив воду и соль.", "Подать гречку с курицей и овощами.")
    ),
    Recipe(
        id = "lunch-2",
        title = "Треска с рисом и кабачком",
        mealType = "lunch",
        description = "Запеченная треска с рисом, кабачком и помидорами.",
        cookingTimeMinutes = 40,
        ingredients = listOf(
            RecipeIngredient("Треска", 150.0, 103.5, 24.0, 0.9, 0.0),
            RecipeIngredient("Рис (крупа)", 50.0, 161.5, 3.5, 0.3, 38.7),
            RecipeIngredient("Кабачки", 100.0, 24.0, 0.6, 0.3, 4.6),
            RecipeIngredient("Помидоры", 50.0, 10.0, 0.6, 0.1, 1.9),
            RecipeIngredient("Масло оливковое", 5.0, 44.9, 0.0, 5.0, 0.0)
        ),
        totalWeightGrams = 506.5,
        totalsKcal = 343.9, totalsProtein = 28.7, totalsFat = 6.6, totalsCarbs = 45.2,
        steps = listOf("Отварить рис в воде до готовности.", "Нарезать кабачок и помидоры.", "Выложить треску и овощи в форму, сбрызнуть оливковым маслом, посолить и поперчить.", "Запекать при 180 °C около 20–25 минут и подать с рисом.")
    ),
    Recipe(
        id = "dinner-1",
        title = "Телятина тушеная с овощами",
        mealType = "dinner",
        description = "Легкий ужин: нежная телятина, капуста, морковь и лук.",
        cookingTimeMinutes = 40,
        ingredients = listOf(
            RecipeIngredient("Телятина", 200.0, 194.0, 39.4, 4.0, 0.0),
            RecipeIngredient("Капуста белокочанная", 150.0, 42.0, 2.7, 0.2, 8.1),
            RecipeIngredient("Морковь", 50.0, 16.5, 0.7, 0.1, 3.5),
            RecipeIngredient("Лук репчатый", 30.0, 12.3, 0.4, 0.1, 2.5),
            RecipeIngredient("Масло оливковое", 5.0, 44.9, 0.0, 5.0, 0.0)
        ),
        totalWeightGrams = 536.5,
        totalsKcal = 309.7, totalsProtein = 43.2, totalsFat = 9.4, totalsCarbs = 14.1,
        steps = listOf("Нарезать телятину и овощи.", "Обжарить телятину на оливковом масле, добавить лук и морковь.", "Добавить капусту, воду, соль и перец, тушить под крышкой до мягкости.")
    ),
    Recipe(
        id = "snack-1",
        title = "Творожный перекус с кефиром и грушей",
        mealType = "snack",
        description = "Легкий белковый перекус из нежирного творога, кефира и груши.",
        cookingTimeMinutes = 5,
        ingredients = listOf(
            RecipeIngredient("Творог нежирный", 100.0, 88.0, 18.0, 0.6, 1.9),
            RecipeIngredient("Кефир нежирный", 100.0, 30.0, 3.0, 0.1, 3.8),
            RecipeIngredient("Груши", 100.0, 42.0, 0.4, 0.0, 10.7)
        ),
        totalWeightGrams = 300.0,
        totalsKcal = 160.0, totalsProtein = 21.4, totalsFat = 0.7, totalsCarbs = 16.4,
        steps = listOf("Смешать нежирный творог с кефиром.", "Добавить нарезанную грушу и перемешать.")
    )
)