# CustomApp - Setup Instructions for Instructors

## API Key Setup

1. Obtain a Perplexity API key from their platform
2. Create or edit the `secrets.properties` file in the project root
3. Add your API key:
   ```
   PERPLEXITY_API_KEY=your_api_key_here
   ```
4. Sync the project with Gradle files (File > Sync Project with Gradle Files)

## Security Notes

- The API key is loaded at build time and stored in `BuildConfig`
- The `secrets.properties` file is in `.gitignore` to prevent accidental commits
- Rate limiting is configured on the API key to prevent abuse
- The key can be revoked at any time if needed

## Testing

1. Build and run the app
2. Enter a claim in the text field
3. Click "Verify Claim" to test the API integration

If you encounter any issues, please contact the developer for assistance.
