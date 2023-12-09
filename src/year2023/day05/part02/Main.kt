package year2023.day05.part02

import utils.readInputFile
import kotlin.time.measureTime

interface MappingRange {
    fun contains(value: Long): Boolean
    fun getMappedValue(value: Long): Long?
}

class ExplicitRange(
    private val destStart: Long,
    private val srcStart: Long,
    private val offset: Long,
): MappingRange {
    private val destEnd = destStart + offset - 1
    private val srcEnd = srcStart + offset - 1

    private val rangePreOffset = destStart - srcStart

    override fun contains(value: Long): Boolean {
        return value in srcStart..srcEnd
    }

    override fun getMappedValue(value: Long): Long? {
        if(!contains(value)) {
            return null
        }
        return rangePreOffset + value

//        return destStart + (value - srcStart)
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
    var seedRanges: List<LongRange> = emptyList(),
    val categoryMappers: List<CategoryMapper> = emptyList()
) {
    fun seedToLocation(seed: Long): Long {
//        return categoryMappers.fold(seed) { value, mapper ->
//            mapper.getMappedValue(value)
//        }
        var value = seed
        for (categoryMapper in categoryMappers) {
            value = categoryMapper.getMappedValue(value)
        }
        return value
    }


    override fun toString(): String {
        return "Almanac(categoryMappers=${categoryMappers})"
    }
}

fun main() {
    val inputLines = readInputFile(trim = false)

    val almanac = parseAlmanac(inputLines)

    var minSeedLocation = Long.MAX_VALUE
    var processedSeeds = 0L
    val totalSeeds = almanac.seedRanges.fold(0L) { acc, range -> acc + range.last - range.first + 1 }
    println("Total seeds: $totalSeeds")

    val duration = measureTime {
        for (seedRange in almanac.seedRanges) {
            for(seed in seedRange.first..seedRange.last) {
                val loc = almanac.seedToLocation(seed)
                if(loc < minSeedLocation) {
                    minSeedLocation = loc
                }

                processedSeeds++

                if(processedSeeds % 1_000_000 == 0L) {
                    println("Processed seeds: $processedSeeds ( ${processedSeeds * 100 / totalSeeds}% )")
                }
            }
        }
    }

    println("Duration: $duration")

    println("Min seed location: $minSeedLocation")
}

fun parseAlmanac(lines: List<String>): Almanac {
    fun parseSeeds(seedLine: String): List<LongRange> {
        val seedParts = seedLine.split(": ")
        val rangeParts = seedParts[1].split(" ").filter { it.isNotBlank() }.map { it.trim() }

        val ranges = mutableListOf<LongRange>()
        for (i in 0..rangeParts.lastIndex step 2) {
            val start = rangeParts[i].toLong()
            val offset = rangeParts[i + 1].toLong()
            val end = start + offset - 1
            ranges += listOf(LongRange(start, end))
        }

        return ranges
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
