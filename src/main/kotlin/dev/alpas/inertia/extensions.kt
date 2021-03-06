package dev.alpas.inertia

import dev.alpas.config
import dev.alpas.http.HttpCall
import dev.alpas.http.RequestableCall
import dev.alpas.make
import dev.alpas.view.Mix

val RequestableCall.isInertia: Boolean
    get() {
        return header("X-Inertia")?.toBoolean() ?: false
    }

fun HttpCall.inertiaRender(component: String, props: Map<String, Any?> = emptyMap()) {
    val inertiaBag = shared("inertia") as InertiaBagBase
    val inertiaValues = inertiaBag(this)
    val combinedProps = props + inertiaValues

    val version = make<Mix>().version
    val page = mapOf("component" to component, "props" to combinedProps, "url" to fullUri, "version" to version)

    if (isInertia) {
        addHeader("Vary" to "Accept", "X-Inertia" to "true")
        replyAsJson(page)
    } else {
        val config = config<InertiaConfig>()
        render(config.rootView, "page" to page)
    }
}
