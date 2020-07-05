package com.zzj.chineseCompletion

import com.github.promeg.pinyinhelper.Pinyin
import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.impl.CamelHumpMatcher

class ChineseCompletionContributor : CompletionContributor() {
    override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
        result.withPrefixMatcher(ChineseAndCamelHumpMatcher(result.prefixMatcher.prefix, false)).also { newResult ->
            newResult.runRemainingContributors(parameters) { newResult.passResult(it) }
        }
    }
}

class ChineseAndCamelHumpMatcher(prefix: String, private val caseSensitive: Boolean) :
    CamelHumpMatcher(prefix, caseSensitive) {

    private fun toPinyin(name: String) = name.toCharArray().joinToString("") {
        if (Pinyin.isChinese(it)) Pinyin.toPinyin(it).toLowerCase().capitalize() else it.toString()
    }

    override fun prefixMatches(name: String) = super.prefixMatches(name).let { matchOld ->
        if (!matchOld && name.any { Pinyin.isChinese(it) }) super.prefixMatches(toPinyin(name)) else matchOld
    }

    override fun cloneWithPrefix(s: String) = takeIf { s == prefix } ?: ChineseAndCamelHumpMatcher(s, caseSensitive)
}
