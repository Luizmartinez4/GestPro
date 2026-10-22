package com.example.gestpro.data.local

/** Dados fixos iniciais, criados na primeira execução se o banco estiver vazio. */
object FarmSeed {

    private data class InputSeed(val name: String, val quantity: Double, val unit: String)

    private data class PlotSeed(val areaHa: Double, val inputs: List<InputSeed>)

    // Posição na lista = número do talhão (1 a 8)
    private val plotSeeds = listOf(
        PlotSeed(
            3.5, listOf(
                InputSeed("Adubo 20-05-20", 620.0, "kg"),
                InputSeed("Herbicida Glifosato", 12.0, "L"),
                InputSeed("Fungicida Cobre", 8.0, "L")
            )
        ),
        PlotSeed(
            4.2, listOf(
                InputSeed("Adubo 20-00-20", 740.0, "kg"),
                InputSeed("Herbicida 2,4-D", 14.0, "L"),
                InputSeed("Fungicida Triazol", 10.0, "L"),
                InputSeed("Inseticida Tiametoxam", 3.0, "L")
            )
        ),
        PlotSeed(
            2.8, listOf(
                InputSeed("Calcário dolomítico", 1200.0, "kg"),
                InputSeed("Adubo 10-10-10", 500.0, "kg"),
                InputSeed("Fungicida Estrobilurina", 6.0, "L")
            )
        ),
        PlotSeed(
            3.0, listOf(
                InputSeed("Adubo 25-05-25", 530.0, "kg"),
                InputSeed("Herbicida Glufosinato", 10.0, "L"),
                InputSeed("Inseticida Imidacloprido", 5.0, "L")
            )
        ),
        PlotSeed(
            2.5, listOf(
                InputSeed("Adubo 20-05-20", 450.0, "kg"),
                InputSeed("Cloreto de potássio", 180.0, "kg"),
                InputSeed("Herbicida Glifosato", 8.0, "L"),
                InputSeed("Fungicida Cobre", 5.0, "L")
            )
        ),
        PlotSeed(
            3.8, listOf(
                InputSeed("Adubo 14-00-18", 670.0, "kg"),
                InputSeed("Fungicida Triazol", 9.0, "L"),
                InputSeed("Inseticida Tiametoxam", 4.0, "L")
            )
        ),
        PlotSeed(
            4.5, listOf(
                InputSeed("Adubo 20-05-20", 800.0, "kg"),
                InputSeed("Herbicida 2,4-D", 15.0, "L"),
                InputSeed("Fungicida Estrobilurina", 11.0, "L"),
                InputSeed("Adubo foliar Boro", 6.0, "L")
            )
        ),
        PlotSeed(
            3.2, listOf(
                InputSeed("Sulfato de amônio", 570.0, "kg"),
                InputSeed("Herbicida Glufosinato", 11.0, "L"),
                InputSeed("Fungicida Cobre", 7.0, "L")
            )
        )
    )

    val farm = FarmEntity(
        id = 1,
        name = "Sítio GestPro São João",
        safra = "2026/27",
        cultivatedAreaHa = plotSeeds.sumOf { it.areaHa }
    )

    val plots: List<PlotEntity> = plotSeeds.mapIndexed { index, seed ->
        PlotEntity(id = (index + 1).toLong(), number = index + 1, areaHa = seed.areaHa)
    }

    val inputs: List<PlotInputEntity> = plotSeeds.flatMapIndexed { index, seed ->
        val plotId = (index + 1).toLong()
        seed.inputs.mapIndexed { order, input ->
            PlotInputEntity(
                plotId = plotId,
                name = input.name,
                quantity = input.quantity,
                unit = input.unit,
                sortOrder = order
            )
        }
    }
}