package com.spinel.gamenotes

import com.spinel.gamenotes.data.AiModelOption
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testAiModelOptionValues() {
    val pro = AiModelOption.GEMINI_PRO_3_1
    val flash = AiModelOption.GEMINI_FLASH_LITE_3_8

    assertEquals("gemini-pro-3.1", pro.modelId)
    assertEquals("gemini-flash-lite-3.8", flash.modelId)
    assertTrue(pro.displayName.contains("Pro 3.1"))
    assertTrue(flash.displayName.contains("Flash Lite 3.8"))
  }
}
