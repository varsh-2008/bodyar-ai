package com.example.data

import com.example.model.BodyRegionId
import kotlinx.coroutines.delay

data class AiAnswer(
    val content: String,
    val suggestedRegion: BodyRegionId? = null,
)

interface AiRepository {
    suspend fun askAnatomyQuestion(
        question: String,
        activeRegion: BodyRegionId,
    ): AiAnswer
}

/**
 * Built-in offline anatomical knowledge base used when Firebase AI Logic is not configured.
 */
class BuiltInAnatomyRepository : AiRepository {

    private data class KnowledgeTopic(
        val keywords: List<String>,
        val region: BodyRegionId? = null,
        val answer: String,
    )

    private val generalTopics = listOf(
        KnowledgeTopic(
            keywords = listOf("heart", "cardiac", "pump", "blood", "circulation", "aorta", "ventricle", "atrium", "cardiovascular"),
            region = BodyRegionId.CHEST,
            answer = "The **heart** is a four-chambered muscular organ seated slightly left of center in the **chest** (thoracic cavity). Deoxygenated blood enters the right atrium and right ventricle, which pump it to the lungs for oxygenation. Oxygen-rich blood then returns to the left atrium and left ventricle, where it is propelled through the **aorta** to nourish the entire body.",
        ),
        KnowledgeTopic(
            keywords = listOf("lung", "lungs", "breathe", "breathing", "respiratory", "oxygen", "alveoli", "diaphragm", "trachea"),
            region = BodyRegionId.CHEST,
            answer = "Your **lungs** sit inside the protective rib cage in the **chest**. During inhalation, the dome-shaped **diaphragm** contracts downward and intercostal muscles lift the ribs, drawing air through the trachea and bronchi into ~300–500 million microscopic **alveoli**, where oxygen diffuses into capillaries and carbon dioxide is exhaled.",
        ),
        KnowledgeTopic(
            keywords = listOf("brain", "neuron", "nerve", "skull", "cranium", "cognition", "memory", "nervous system"),
            region = BodyRegionId.HEAD,
            answer = "Protected within the **cranium** of the **head**, the **brain** contains roughly 86 billion neurons connected by trillions of synapses. The **cerebrum** handles thought, sensory perception, and voluntary movement; the **cerebellum** refines balance and coordination; and the **brainstem** regulates vital autonomic functions like heart rate and breathing.",
        ),
        KnowledgeTopic(
            keywords = listOf("rotator cuff", "clavicle", "collarbone", "scapula", "deltoid", "shoulder"),
            region = BodyRegionId.SHOULDERS,
            answer = "The **shoulder complex** pairs the **clavicle**, **scapula**, and **humerus** in a highly mobile ball-and-socket arrangement. Because the socket (glenoid) is shallow, four **rotator cuff** muscles act as dynamic stabilizers while the **deltoid** powers arm elevation.",
        ),
        KnowledgeTopic(
            keywords = listOf("biceps", "triceps", "humerus", "radius", "ulna", "elbow", "forearm", "arm"),
            region = BodyRegionId.ARMS,
            answer = "The **arm** uses a lever system: the **humerus** bridges shoulder to elbow, while the **radius** and **ulna** run parallel in the forearm. The **biceps brachii** flexes the elbow and supinates the forearm, while the **triceps brachii** extends it. Forearm rotation happens as the radius pivots smoothly over the ulna.",
        ),
        KnowledgeTopic(
            keywords = listOf("hand", "hands", "finger", "fingers", "thumb", "wrist", "carpal", "grip", "tendon"),
            region = BodyRegionId.HANDS,
            answer = "Each **hand** contains **27 bones** (8 carpals, 5 metacarpals, 14 phalanges). Fingers contain no muscles of their own—long tendons connect them to muscles in your forearm, while intrinsic hand muscles inside the palm allow your **opposable thumb** to execute delicate precision tasks.",
        ),
        KnowledgeTopic(
            keywords = listOf("stomach", "liver", "intestine", "gut", "digestion", "digestive", "kidney", "core", "abs", "abdomen", "pancreas"),
            region = BodyRegionId.ABDOMEN,
            answer = "The **abdomen** houses major digestive and metabolic organs—including the **stomach**, **liver**, **pancreas**, and **intestines**—shielded anteriorly by layered core muscles (**rectus abdominis**, **obliques**, and **transversus abdominis**) rather than bone, allowing expansion during meals and deep breathing.",
        ),
        KnowledgeTopic(
            keywords = listOf("femur", "knee", "patella", "tibia", "quadriceps", "hamstring", "calf", "thigh", "leg"),
            region = BodyRegionId.LEGS,
            answer = "Your **legs** rely on the **femur** (the body's longest, strongest bone) and the **tibia and fibula** below the knee. Powerful **quadriceps** straighten the knee while **hamstrings** flex it and extend the hip. Inside the knee, the **ACL, PCL, MCL, LCL**, and **menisci** absorb shock and stabilize motion.",
        ),
        KnowledgeTopic(
            keywords = listOf("foot", "feet", "ankle", "heel", "achilles", "arch", "toe", "plantar"),
            region = BodyRegionId.FEET,
            answer = "Each **foot** integrates **26 bones**, **33 joints**, and over **100 muscles, tendons, and ligaments**. Its longitudinal and transverse **arches** act like natural leaf springs, absorbing impact through the **plantar fascia** and storing elastic recoil in the **Achilles tendon**.",
        ),
        KnowledgeTopic(
            keywords = listOf("skeleton", "skeletal", "bone", "bones", "206", "marrow", "spine", "vertebra"),
            answer = "An adult human skeleton has **206 bones** divided into the **axial skeleton** (skull, spine, rib cage—80 bones) and **appendicular skeleton** (shoulders, arms, pelvis, legs—126 bones). Beyond structural support and organ protection, bones store calcium and manufacture blood cells inside **bone marrow**.",
        ),
        KnowledgeTopic(
            keywords = listOf("muscle", "muscular", "skeletal muscle", "contraction", "strength"),
            answer = "The human body contains over **600 skeletal muscles** controlled voluntarily via motor neurons, alongside involuntary **smooth muscle** (blood vessels, organs) and **cardiac muscle** (heart). Muscles only pull—never push—so they operate in opposing pairs like the biceps and triceps.",
        ),
    )

    override suspend fun askAnatomyQuestion(
        question: String,
        activeRegion: BodyRegionId,
    ): AiAnswer {
        delay(220)
        val trimmed = question.trim()
        val lower = trimmed.lowercase()

        val mentionedRegion = detectRegionMention(lower)
        val targetRegionId = mentionedRegion ?: activeRegion
        val targetRegion = AnatomyCatalog.getRegion(targetRegionId)

        val asksBones = Regex("\\b(bone|bones|skeleton|skeletal|anatomy|structure|organs|muscles)\\b").containsMatchIn(lower)
        val asksFunction = Regex("\\b(function|functions|purpose|do|does|role|work|works)\\b").containsMatchIn(lower)
        val asksFact = Regex("\\b(fact|facts|interesting|cool|trivia|fun)\\b").containsMatchIn(lower)
        val asksSystems = Regex("\\b(system|systems|connected|related)\\b").containsMatchIn(lower)
        val referencesContext = Regex("\\b(this|here|selected|region|it)\\b").containsMatchIn(lower)

        if (referencesContext) {
            if (asksFact) {
                return AiAnswer(
                    content = "Fascinating facts about the **${targetRegion.name}**:\n\n" +
                        targetRegion.interestingFacts.joinToString("\n") { "• $it" },
                    suggestedRegion = targetRegionId,
                )
            }
            if (asksSystems) {
                return AiAnswer(
                    content = "The **${targetRegion.name}** primarily collaborates with these body systems:\n\n" +
                        targetRegion.relatedSystems.joinToString("\n") { "• $it" } +
                        "\n\n${targetRegion.summary}",
                    suggestedRegion = targetRegionId,
                )
            }
            if (asksBones) {
                val groups = targetRegion.associatedAnatomy.joinToString("\n") { group ->
                    "• **${group.category}:** ${group.items.joinToString(", ")}"
                }
                return AiAnswer(
                    content = "Key anatomical structures in the **${targetRegion.name} (${targetRegion.clinicalName})**:\n\n$groups",
                    suggestedRegion = targetRegionId,
                )
            }
            if (asksFunction) {
                return AiAnswer(
                    content = "Primary physiological functions of the **${targetRegion.name}**:\n\n" +
                        targetRegion.functions.joinToString("\n") { "• $it" },
                    suggestedRegion = targetRegionId,
                )
            }
        }

        var bestTopic: KnowledgeTopic? = null
        var bestScore = 0
        for (topic in generalTopics) {
            var score = 0
            for (kw in topic.keywords) {
                if (lower.contains(kw)) {
                    score += if (kw.length > 5) 2 else 1
                }
            }
            if (score > bestScore) {
                bestScore = score
                bestTopic = topic
            }
        }

        if (mentionedRegion != null) {
            if (asksFact) {
                return AiAnswer(
                    content = "Fascinating facts about the **${targetRegion.name}**:\n\n" +
                        targetRegion.interestingFacts.joinToString("\n") { "• $it" },
                    suggestedRegion = targetRegionId,
                )
            }
            if (asksSystems) {
                return AiAnswer(
                    content = "The **${targetRegion.name}** primarily collaborates with these body systems:\n\n" +
                        targetRegion.relatedSystems.joinToString("\n") { "• $it" } +
                        "\n\n${targetRegion.summary}",
                    suggestedRegion = targetRegionId,
                )
            }
            if (asksBones && bestScore <= 1) {
                val groups = targetRegion.associatedAnatomy.joinToString("\n") { group ->
                    "• **${group.category}:** ${group.items.joinToString(", ")}"
                }
                return AiAnswer(
                    content = "Key anatomical structures in the **${targetRegion.name} (${targetRegion.clinicalName})**:\n\n$groups",
                    suggestedRegion = targetRegionId,
                )
            }
        }

        if (bestTopic != null && bestScore > 0) {
            return AiAnswer(
                content = bestTopic.answer,
                suggestedRegion = bestTopic.region,
            )
        }

        if (mentionedRegion != null) {
            if (asksFunction) {
                return AiAnswer(
                    content = "Primary physiological functions of the **${targetRegion.name}**:\n\n" +
                        targetRegion.functions.joinToString("\n") { "• $it" },
                    suggestedRegion = targetRegionId,
                )
            }
            return AiAnswer(
                content = formatRegionOverview(mentionedRegion),
                suggestedRegion = mentionedRegion,
            )
        }

        if (Regex("\\b(hi|hello|hey|help|what can you)\\b").containsMatchIn(lower)) {
            return AiAnswer(
                content = "Hello! I'm the **BodyAR AI Assistant** (running on the built-in Anatomy Knowledge Base). " +
                    "You currently have **${targetRegion.name}** selected.\n\n" +
                    "Try asking:\n" +
                    "• *What bones and muscles are in this region?*\n" +
                    "• *How do the heart and lungs work together?*\n" +
                    "• *Tell me an interesting fact about the hands.*\n" +
                    "• *Explain the skeletal system.*",
            )
        }

        return AiAnswer(
            content = "Here is a built-in anatomy summary for your active focus (**${targetRegion.name}**):\n\n" +
                formatRegionOverview(activeRegion) +
                "\n\nTip: Ask specifically about organs (*heart*, *brain*, *lungs*, *liver*), joints (*shoulder*, *knee*), or select any body region above.",
        )
    }

    private fun detectRegionMention(lower: String): BodyRegionId? {
        val aliases: Map<BodyRegionId, List<String>> = mapOf(
            BodyRegionId.HEAD to listOf("head", "skull", "face", "brain", "cranium", "eye", "ear", "jaw"),
            BodyRegionId.SHOULDERS to listOf("shoulder", "shoulders", "clavicle", "collarbone", "rotator", "deltoid", "scapula"),
            BodyRegionId.CHEST to listOf("chest", "thorax", "thoracic", "rib", "ribs", "sternum", "heart", "lung", "pectoral"),
            BodyRegionId.ARMS to listOf("arm", "arms", "elbow", "forearm", "bicep", "tricep", "humerus"),
            BodyRegionId.HANDS to listOf("hand", "hands", "wrist", "finger", "thumb", "palm", "carpal"),
            BodyRegionId.ABDOMEN to listOf("abdomen", "abdominal", "stomach", "belly", "core", "abs", "liver", "intestine", "gut"),
            BodyRegionId.LEGS to listOf("leg", "legs", "thigh", "knee", "femur", "shin", "tibia", "quad", "hamstring", "calf"),
            BodyRegionId.FEET to listOf("foot", "feet", "ankle", "toe", "heel", "arch", "achilles", "plantar"),
        )
        for ((regionId, words) in aliases) {
            if (words.any { word -> Regex("\\b$word\\b").containsMatchIn(lower) }) {
                return regionId
            }
        }
        return null
    }

    private fun formatRegionOverview(regionId: BodyRegionId): String {
        val r = AnatomyCatalog.getRegion(regionId)
        val firstAnatomy = r.associatedAnatomy.firstOrNull()?.items?.take(4)?.joinToString(", ").orEmpty()
        return "**${r.name} (${r.clinicalName})**\n\n" +
            "${r.summary}\n\n" +
            "• **Key Anatomy:** $firstAnatomy\n" +
            "• **Primary Function:** ${r.functions.first()}\n" +
            "• **Fun Fact:** ${r.interestingFacts.first()}\n" +
            "• **Related Systems:** ${r.relatedSystems.joinToString(", ")}"
    }
}
