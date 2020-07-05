package com.zzj.chineseCompletion

import com.github.promeg.pinyinhelper.Pinyin
import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.impl.CamelHumpMatcher

class ChineseCompletionContributor : CompletionContributor() {
    override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
        super.fillCompletionVariants(parameters, result)
        result.withPrefixMatcher(ChineseAndCamelHumpMatcher(result.prefixMatcher.prefix, false)).also { newResult ->
            newResult.runRemainingContributors(parameters) { newResult.addElement(it.lookupElement) }
        }
    }
}

class ChineseAndCamelHumpMatcher(prefix: String, caseSensitive: Boolean) : CamelHumpMatcher(prefix, caseSensitive) {
    private val matchResultCache = mutableMapOf<String, Boolean>()
    private val isChineseCache = mutableMapOf<String, Boolean>()

    private fun isChinese(name: String) = isChineseCache.getOrPut(name, { name.any { Pinyin.isChinese(it) } })

    private fun toPinyin(name: String) = name.toCharArray().joinToString("") {
        if (Pinyin.isChinese(it))
            Pinyin.toPinyin(it).run { first() + drop(1).toLowerCase() }
        else
            it.toString()
    }

    override fun prefixMatches(name: String): Boolean {
        val matchOld = super.prefixMatches(name)
        if (!matchOld && isChinese(name)) {
            return matchResultCache.getOrPut(name, { super.prefixMatches(toPinyin(name)) })
        }
        return matchOld
    }
}
