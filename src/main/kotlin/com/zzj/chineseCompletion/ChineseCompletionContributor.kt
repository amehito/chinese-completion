package com.zzj.chineseCompletion

import com.github.promeg.pinyinhelper.Pinyin
import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.impl.CamelHumpMatcher
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.codeInsight.lookup.LookupElementPresentation
import com.intellij.codeInsight.lookup.LookupElementRenderer

class ChineseCompletionContributor : CompletionContributor() {

    override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
        super.fillCompletionVariants(parameters, result)
        val myMatcher = ChineseAndCamelHumpMatcher(result.prefixMatcher.prefix, false)
        result.withPrefixMatcher(myMatcher)
            .runRemainingContributors(parameters) { el ->
                val ele = el.lookupElement
                val newName = myMatcher.nameMap[ele.lookupString]
                if (newName != null) {
                    result.addElement(
                        LookupElementBuilder.create(newName)
                            .withRenderer(object : LookupElementRenderer<LookupElement>() {
                                override fun renderElement(
                                    element: LookupElement?,
                                    presentation: LookupElementPresentation?
                                ) {
                                    ele.renderElement(presentation)
                                }
                            })
                            .withInsertHandler { ctx, _ ->
                                ctx.document.replaceString(
                                    ctx.startOffset,
                                    ctx.tailOffset,
                                    ele.lookupString
                                )
                            }
                    )
                } else {
                    result.addElement(ele)
                }
            }
    }
}

class ChineseAndCamelHumpMatcher(prefix: String, caseSensitive: Boolean) : CamelHumpMatcher(prefix, caseSensitive) {
    val nameMap = mutableMapOf<String, String>()
    override fun prefixMatches(name: String): Boolean {
        val matchOld = super.prefixMatches(name)
        if (!matchOld && name.any { Pinyin.isChinese(it) }) {
            val newName = name
                .toCharArray()
                .joinToString("") {
                    if (Pinyin.isChinese(it))
                        "$it(${Pinyin.toPinyin(it).toLowerCase()})"
                    else
                        "$it"
                }
            if (super.prefixMatches(newName)) {
                nameMap[name] = newName
                return true
            }
        }
        return matchOld
    }
}
