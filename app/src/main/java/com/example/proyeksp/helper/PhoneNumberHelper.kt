package com.example.proyeksp.helper

class PhoneNumberHelper {
    companion object {
        fun formatToE164(phoneNumber: String): String {
            // Remove all non-numeric characters except a leading '+' if present
            val cleaned = phoneNumber.replace(Regex("[^0-9+]"), "")

            return when {
                // If it already starts with '+' (e.g., +628123456)
                cleaned.startsWith("+") -> cleaned

                // If it starts with '62' but lacks '+' (e.g., 628123456)
                cleaned.startsWith("62") -> "+$cleaned"

                // If it starts with local prefix '0' (e.g., 08123456) -> replace '0' with '+62'
                cleaned.startsWith("0") -> "+62" + cleaned.substring(1)

                // Fallback: If it's a raw number without zero (e.g., 8123456)
                else -> "+62$cleaned"
            }
        }
    }
}