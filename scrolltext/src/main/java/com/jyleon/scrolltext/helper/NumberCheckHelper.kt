package com.jyleon.scrolltext.helper


import java.math.BigDecimal
import java.math.BigInteger
import kotlin.math.max

/**
 * @Name NumberCheckHelper
 * @Descript TODO
 * @CreateTime 2024/8/14 11:36
 * @Created by Administrator
 */
object NumberCheckHelper {

    const val TAG = "NumberCheckHelper"

    /**
     * 校验数字的合法性
     *
     * @param numStart
     * @param numEnd
     * @return
     */
    public fun checkNumString(numStart: String, numEnd: String): Boolean {
        try {
            val start = BigDecimal(numStart) // 起始数字小数的筛选
            val end = BigDecimal(numEnd) // 最终数字小数的筛选

            if (start >= end){
                return false
            }
        } catch (e: Exception) {

            return false
        }

        return compareNumbers(numStart,numEnd)
    }

    fun sortDigitsDescending(input: String): List<Int> {
        // 移除小数点
        val cleanedInput = input.replace(".", "")

        // 将每个字符转换为整数并加入列表
        val digitsList = cleanedInput.map { it.toString().toInt() }

        // 按照从大到小的顺序排序
        return digitsList.sortedDescending()
    }



    fun compareNumbers(num1: String, num2: String): Boolean {
        // 先将字符串按小数点分割
        val parts1 = num1.split(".")
        val parts2 = num2.split(".")

        // 构建完整的数组，包含整数部分、小数点、和小数部分
        val array1 = parts1[0].toCharArray() + '.' + (if (parts1.size > 1) parts1[1].toCharArray() else charArrayOf())
        val array2 = parts2[0].toCharArray() + '.' + (if (parts2.size > 1) parts2[1].toCharArray() else charArrayOf())



//        startList.addAll(array1.toList())
//        endList.addAll(array2.toList())


//        LogManager.i("cache list start: ${startList}")
//        LogManager.i("cache list end: ${endList}")


        // 将字符串转为字符数组
        val integerPart1 = parts1[0].toCharArray()
        val integerPart2 = parts2[0].toCharArray()

        val decimalPart1 = if (parts1.size > 1) parts1[1].toCharArray() else charArrayOf()
        val decimalPart2 = if (parts2.size > 1) parts2[1].toCharArray() else charArrayOf()

        // 打印数组内容



        // 比较整数部分和小数部分的长度是否相同
        return integerPart1.size == integerPart2.size && decimalPart1.size == decimalPart2.size
    }


    fun processNumberString(input: String): List<Int> {
        val cacheList = mutableListOf<Int>()
        // 判断是否包含小数点
        return if (input.contains(".")) {
            // 分割整数部分和小数部分
            val parts = input.split(".")
            val integerPart = parts[0].toInt()
            val decimalPart = parts[1].toInt()
            cacheList.add(integerPart)
            cacheList.add(decimalPart)
            cacheList.toList()
        } else {
             // 如果全是整数，返回整数
            cacheList.add(input.toInt())
            cacheList.toList()
        }
    }

    fun generateNumberRange(start: Int, end: Int): List<Int> {
        // 校验输入数字是否在合法范围内（0-9）
        require(start in 0..9 && end in 0..9) { "Both start and end must be single-digit numbers (0-9)" }

        // 如果起始数字小于等于结束数字，直接生成范围内的数字列表
        return if (start <= end) {
            (start..end).toList()
        } else {
            // 如果起始数字大于结束数字，生成跨越0的数字列表
            (start..9).toList() + (0..end).toList()
        }
    }

    fun generateCharRange(start: Int, end: Int): List<Char> {
        // 校验输入数字是否在合法范围内（0-9）
        require(start in 0..9 && end in 0..9) { "Both start and end must be single-digit numbers (0-9)" }

        // 如果起始数字小于等于结束数字，直接生成范围内的字符列表
        return if (start <= end) {
            (start..end).map { it.digitToChar() }
        } else {
            // 如果起始数字大于结束数字，生成跨越0的字符列表
            (start..9).map { it.digitToChar() } + (0..end).map { it.digitToChar() }
        }
    }


    fun generateNumberRangeWithDecimal(start: String, end: String): List<String> {
        val result = mutableListOf<String>()

        // 拆分整数部分和小数部分
        val startParts = start.split(".").map { it.toCharArray() }
        val endParts = end.split(".").map { it.toCharArray() }

        // 初始化当前的整数部分和小数部分
        var currentIntegerPart = startParts[0]
        var currentDecimalPart = if (startParts.size > 1) startParts[1] else charArrayOf()

        val endIntegerPart = endParts[0]
        val endDecimalPart = if (endParts.size > 1) endParts[1] else charArrayOf()

        while (true) {
            // 构建当前的数字
            val currentNumber = String(currentIntegerPart) + if (currentDecimalPart.isNotEmpty()) {
                "." + String(currentDecimalPart)
            } else {
                ""
            }

            // 添加当前数字到结果中
            result.add(currentNumber)

            // 逐位比较和增加
            var carry = false

            // 处理小数部分
            for (i in currentDecimalPart.indices.reversed()) {
                if (currentDecimalPart[i] < endDecimalPart[i]) {
                    currentDecimalPart[i]++
                    carry = false
                } else if (currentDecimalPart[i] == endDecimalPart[i] && carry) {
                    currentDecimalPart[i]++
                    carry = false
                } else if (currentDecimalPart[i] > endDecimalPart[i]) {
                    currentDecimalPart[i] = '0'
                    carry = true
                }
            }

            // 处理整数部分
            for (i in currentIntegerPart.indices.reversed()) {
                if (currentIntegerPart[i] < endIntegerPart[i] || carry) {
                    currentIntegerPart[i]++
                    carry = false
                } else if (currentIntegerPart[i] == endIntegerPart[i] && carry) {
                    currentIntegerPart[i]++
                    carry = false
                } else if (currentIntegerPart[i] > endIntegerPart[i]) {
                    currentIntegerPart[i] = '0'
                    carry = true
                }
            }

            // 检查是否到达目标数字
            if (currentIntegerPart.contentEquals(endIntegerPart) && currentDecimalPart.contentEquals(endDecimalPart)) {
                result.add(end)
                break
            }
        }

        return result
    }

    fun getNumIntervalList(startNum: String,endNum:String):List<List<Char>>{
        val cacheRow = mutableListOf<List<Char>>()

        val cacheInput = padNumbersToEqualLength(startNum,endNum)

        val startPad = cacheInput[0]
        val endPad = cacheInput[1]

//        val maxLen = max(startNum.length,endNum.length)

        for (i in startPad.indices){
            val cacheColumn = mutableListOf<Char>()
            if (startPad[i]==' ' ||startPad[i]=='.'||endPad[i]==' ' ||endPad[i]=='.'){
                cacheColumn.add(startPad[i])
                cacheColumn.add(endPad[i])
            }else{
                cacheColumn.addAll(generateCharRange(startPad[i].digitToInt(),endPad[i].digitToInt()))
            }

            cacheRow.add(cacheColumn)
        }

        return cacheRow
    }

    fun generateCharArray(start: Char, end: Char): CharArray? {
        // 使用正则表达式检查输入是否为数字字符
        val regex = Regex("[0-9]")
        if (!start.toString().matches(regex) || !end.toString().matches(regex)) return null

        val resultList = mutableListOf<Char>()

        var current = start.digitToInt()
        val endInt = end.digitToInt()

        while (true) {
            resultList.add(current.digitToChar())
            if (current == endInt) break
            current = (current + 1) % 10
        }

        return resultList.toCharArray()
    }

    fun padNumbersToEqualLength(num1: String, num2: String): List<String> {
        // 计算两个数字的整数部分和小数部分的最大长度

        var cacheInput1 = num1
        var cacheInput2 = num2

        val containsDecimal1 = cacheInput1.contains(".")
        val containsDecimal2 = cacheInput2.contains(".")

        if (!containsDecimal1){
            cacheInput1 = "$cacheInput1."
        }

        if (!containsDecimal2){
            cacheInput2 = "$cacheInput2."
        }

        val parts1 = cacheInput1.split(".")
        val parts2 = cacheInput2.split(".")

        val intPartLength = maxOf(parts1[0].length, parts2[0].length)
        val decimalPartLength = maxOf(parts1.getOrElse(1) { "" }.length, parts2.getOrElse(1) { "" }.length)

        // 补全整数部分和小数部分
        val paddedNum1 = parts1[0].padStart(intPartLength, '0') + "." + parts1.getOrElse(1) { "" }.padEnd(decimalPartLength, '0')
        val paddedNum2 = parts2[0].padStart(intPartLength, '0') + "." + parts2.getOrElse(1) { "" }.padEnd(decimalPartLength, '0')

        var paddedCache1 = paddedNum1
        var paddedCache2 = paddedNum2

//        if (!containsDecimal1){
//            paddedCache1 = paddedNum1.replace("."," ")
//        }
//
//        if (!containsDecimal2){
//            paddedCache2.replace("."," ")
//        }

        // 返回结果列表
        return listOf(paddedCache1, paddedCache2)
    }

    fun getLargerString(str1: String, str2: String): String {
        return if (str1.length > str2.length) {
            str1
        } else if (str1.length < str2.length) {
            str2
        } else {
            // 如果长度相同，返回字典序较大的字符串
            if (str1 > str2) str1 else str2
        }
    }




}