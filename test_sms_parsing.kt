fun main() {
    // Test HDFC SMS format
    val hdfcSms = """Sent Rs.5.00
From HDFC Bank A/C x7805
To APPLE MEDIA SERVICES
On 09/12/24
Ref 434452012929
Not You?
Call 18002586161/SMS BLOCK UPI to 7308080808"""

    println("Testing HDFC SMS parsing:")
    println("SMS Body: $hdfcSms")
    println("---")

    val lower = hdfcSms.lowercase()
    println("Lowercase: $lower")
    println("Contains 'sent': ${lower.contains("sent")}")
    println("---")

    // Test regex patterns
    val amountOfPattern = Regex("""amount of (?:Rs\.?|INR|Rs|₹)\s*(\d+(?:[.,]\d+)?)""", RegexOption.IGNORE_CASE)
    val mainAmountRegex = Regex("""(?:Rs\.?|INR|Rs|₹)\s*(\d+(?:[.,]\d+)?)""", RegexOption.IGNORE_CASE)
    val altAmountRegex = Regex("""(\d+(?:[.,]\d+)?)\s*(?:Rs\.?|INR|rupees)""", RegexOption.IGNORE_CASE)
    val simpleAmountRegex = Regex("""(\d+(?:[.,]\d+)?)""")
    val sentPattern = Regex("""sent\s+Rs\.(\d+(?:\.\d+)?)""", RegexOption.IGNORE_CASE)

    println("Testing regex patterns:")
    println("Sent pattern: ${sentPattern.find(hdfcSms)?.groupValues}")
    println("Amount of pattern: ${amountOfPattern.find(hdfcSms)?.groupValues}")
    println("Main amount pattern: ${mainAmountRegex.find(hdfcSms)?.groupValues}")
    println("Alt amount pattern: ${altAmountRegex.find(hdfcSms)?.groupValues}")
    println("Simple amount pattern: ${simpleAmountRegex.find(hdfcSms)?.groupValues}")

    // Test the final extraction logic
    val sentMatch = sentPattern.find(hdfcSms)
    val amountOfMatch = amountOfPattern.find(hdfcSms)
    val mainMatch = mainAmountRegex.find(hdfcSms)
    val altMatch = altAmountRegex.find(hdfcSms)
    val simpleMatch = simpleAmountRegex.find(hdfcSms)

    val amount = sentMatch?.groupValues?.get(1)
        ?: amountOfMatch?.groupValues?.get(1)
        ?: mainMatch?.groupValues?.get(1)
        ?: altMatch?.groupValues?.get(1)
        ?: simpleMatch?.groupValues?.get(1) ?: "N/A"

    println("---")
    println("Final extracted amount: $amount")
    println("Amount != N/A: ${amount != "N/A"}")
}
