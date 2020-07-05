package com.zzj.chineseCompletion

import com.github.promeg.pinyinhelper.Pinyin
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.completion.impl.CamelHumpMatcher
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.codeInsight.lookup.LookupElementPresentation
import com.intellij.codeInsight.lookup.LookupElementRenderer

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

    override fun prefixMatches(name: String): Boolean {
        val matchOld = super.prefixMatches(name)
        if (!matchOld && name.any { Pinyin.isChinese(it) }) {
            return super.prefixMatches(toPinyin(name))
        }
        return matchOld
    }

    override fun cloneWithPrefix(s: String): PrefixMatcher {
        return takeIf { s == prefix } ?: ChineseAndCamelHumpMatcher(s, caseSensitive)
    }
}
