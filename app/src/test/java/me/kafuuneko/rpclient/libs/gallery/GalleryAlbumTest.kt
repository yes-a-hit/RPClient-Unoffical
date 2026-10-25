package me.kafuuneko.rpclient.libs.gallery

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 相册归属与筛选。
 *
 * 相册和标签是两套东西：标签是给模型检索的多值索引，
 * 相册是一张图只属于一个地方、给人翻的。混成一体会导致
 * 用户既没法按相册找图，也说不清一张图到底归属哪里。
 */
class GalleryAlbumTest {

    private fun image(id: String, album: String) = GalleryImage(
        id = id,
        name = id,
        fileUuid = "uuid-$id",
        album = album
    )

    /** 和 GalleryViewModel 里的筛选口径一致。 */
    private fun filter(images: List<GalleryImage>, album: String?) = if (album == null) {
        images
    } else {
        images.filter { it.album.trim() == album }
    }

    private val sample = listOf(
        image("a", "海"),
        image("b", "海"),
        image("c", "梗图"),
        image("d", "")
    )

    @Test
    fun `null means no filter`() {
        assertEquals(4, filter(sample, null).size)
    }

    @Test
    fun `empty string means unfiled only`() {
        val unfiled = filter(sample, "")
        assertEquals(1, unfiled.size)
        assertEquals("d", unfiled.single().id)
    }

    @Test
    fun `named album groups its own images`() {
        assertEquals(listOf("a", "b"), filter(sample, "海").map { it.id })
    }

    @Test
    fun `null and empty are different buckets`() {
        // 这条就是那个坑：如果都当成「全部」，「未分类」这个真实分组永远点不到
        assertFalse(filter(sample, null).size == filter(sample, "").size)
    }

    @Test
    fun `album is trimmed on comparison`() {
        val padded = listOf(image("x", " 海 "))
        assertEquals(1, filter(padded, "海").size)
    }

    @Test
    fun `new image defaults to unfiled`() {
        assertTrue(image("z", "").album.isBlank())
    }
}
