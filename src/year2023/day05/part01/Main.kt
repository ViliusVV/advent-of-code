package year2023.day05.part01

import utils.readInputFileLines
import utils.stringify

interface MappingRange {
    fun contains(value: Long): Boolean
    fun getMappedValue(value: Long): Long?
}

class ExplicitRange(
    private val destStart: Long,
    private val srcStart: Long,
    private val offset: Long,
): MappingRange {
    private val destEnd by lazy { destStart + offset - 1 }
    private val srcEnd by lazy { srcStart + offset - 1  }

    override fun contains(value: Long): Boolean {
        return value in srcStart..srcEnd
    }

    override fun getMappedValue(value: Long): Long? {
        if(!contains(value)) {
            return null
        }

        return destStart + (value - srcStart)
    }

    override fun toString(): String {
        return "($srcStart to $srcEnd -> $destStart to $destEnd)"
    }
}

class CategoryMapper (
    private val name: String,
    private val ranges: List<ExplicitRange>
) {
    fun getMappedValue(value: Long): Long {
        for (range in ranges) {
            val mappedValue = range.getMappedValue(value)
            if(mappedValue != null) {
                return mappedValue
            }
        }

        return value
    }

    override fun toString(): String {
        return name
    }
}

data class Almanac (
    var seeds: List<Long> = emptyList(),
    val categoryMappers: List<CategoryMapper> = emptyList()
) {
    fun seedToLocation(seed: Long): Long {
        return categoryMappers.fold(seed) { value, mapper ->
            mapper.getMappedValue(value)
        }
    }

    override fun toString(): String {
        return "Almanac(categoryMappers=${categoryMappers})"
    }
}

fun main() {
    val inputLines = readInputFileLines("year2023/day05/input.data")

    val almanac = parseAlmanac(inputLines)

    val seedToLocations = almanac.seeds.associateWith { almanac.seedToLocation(it) }
    println("Seed -> Location:\n${seedToLocations.stringify()}")

    val minSeedLoc = seedToLocations.minByOrNull { it.value }!!.value
    println("Min seed location: $minSeedLoc")
}

fun parseAlmanac(lines: List<String>): Almanac {
    fun parseSeeds(seedLine: String): List<Long> {
        val seedParts = seedLine.split(": ")
        return seedParts[1].split(" ").filter { it.isNotBlank() }.map { it.trim().toLong() }
    }

    fun parseExplicitRange(line: String): ExplicitRange {
        val parts = line.split(" ")
        val destStart = parts[0].trim().toLong()
        val srcStart = parts[1].trim().toLong()
        val offset = parts[2].trim().toLong()

        return ExplicitRange(destStart, srcStart, offset)
    }

    fun parseCategoryMappingRanges(start: Int): CategoryMapper {
        val categoryMapperParts = lines[start].split("map:")
        val categoryName = categoryMapperParts[0].trim()
        val explicitRanges = mutableListOf<ExplicitRange>()

        var lineIdx = start + 1
        while (lineIdx <= lines.lastIndex) {
            val line = lines[lineIdx++]

            if(line.isBlank()) {
                break
            }

            explicitRanges += parseExplicitRange(line)
        }

        return CategoryMapper(categoryName, explicitRanges)
    }

    val mappers = mutableListOf<CategoryMapper>()
    val seeds = parseSeeds(lines[0])

    for (i in (1..lines.lastIndex)) {
        val line = lines[i]

        if(line.contains("map:")) {
            val categoryMapper = parseCategoryMappingRanges(i)
            mappers.add(categoryMapper)
        }
    }


    return Almanac(seeds, mappers)
}
