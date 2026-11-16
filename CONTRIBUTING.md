# Contributing to iBAS Sahayak

Thank you for your interest in improving **iBAS Sahayak**!

## Development Guidelines

1. **Fork & Branch:** Create a feature branch from `main` (e.g., `feat/pay-fixation-calculator`).
2. **Code Style:** Follow official Kotlin formatting (`kotlin.code.style=official`) and keep Composables modular.
3. **Domain Rules:** Any updates to Bangladesh Government Pay Scale, GPF, Pension, or Leave rules should include corresponding JUnit tests in `app/src/test/java/com/example/`.
4. **Verify Before PR:**
   ```bash
   gradle testDebugUnitTest
   gradle assembleDebug
   ```
5. **No Secrets:** Never commit `.env`, `local.properties`, `*.jks`, or `google-services.json`.
