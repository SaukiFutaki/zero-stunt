# Zero Stunt - Child Nutrition Monitoring App

## Overview
Zero Stunt is an innovative application that leverages artificial intelligence and image recognition technology to help parents monitor their child's growth and development. The app can predict the risk of stunting based on the child's condition data, detect the nutritional content of baby food using a camera, and provide personalized food recommendations to meet the child's nutritional needs. With features for reminders and regular tracking, Zero Stunt supports parents in ensuring their children receive adequate nutrition for optimal growth.
The app provides :
- Stunting risk prediction
- Baby food nutritional content detection
- Personalized food recommendations
- Growth and development tracking reminders

## Mobile Development Team
- Lucky Barga Aretama (A200B4KY2291)
- Muhammad Sauki Futaki Wahid (A800B4NY3084)

## Tech Stack & Libraries

### Core Technologies
- **Language**: Kotlin
- **Minimum SDK**: Level 24
- **Architecture**:
  - MVVM (Model-View-ViewModel)
  - Repository Pattern

### Key Dependencies
#### Android & Jetpack
- `androidx.core:core-ktx`
- `androidx.appcompat`
- `androidx.constraintlayout`
- `androidx.lifecycle:lifecycle-livedata-ktx`
- `androidx.lifecycle:lifecycle-viewmodel-ktx`
- `androidx.navigation:navigation-fragment-ktx`
- `androidx.navigation:navigation-ui-ktx`
- `androidx.core:core-splashscreen`
- `androidx.work:work-runtime-ktx`

#### Camera & Image Processing
- `androidx.camera:camera-camera2`
- `androidx.camera:camera-lifecycle`
- `androidx.camera:camera-view`
- `androidx.camera:camera-extensions`

#### Network & API
- `Retrofit2`
- `OkHttp3`
- `Gson Converter`

#### Machine Learning
- `TensorFlow Lite`
- `TensorFlow Lite Support`
- `TensorFlow Lite Metadata`

#### Database
- `androidx.room:room-runtime`
- `androidx.room:room-ktx`

#### UI Components
- `Material Design`
- `Circle ImageView`


### Development Tools
- Android Studio
- Kotlin
- Gradle

## Design
- UI/UX Design: [Figma Design](https://www.figma.com/design/B1DFEuYqBZcdLxc1URT4im/Untitled?node-id=0-1&node-type=canvas&t=xp6qOpfS5w83zQVo-0)

## Features
1. **Stunting Prediction**: Predicts stunting risk based on child's condition data
2. **Food Recognition**: Detects nutritional content of baby food via camera
3. **Personalized Recommendations**: Provides tailored nutritional advice
4. **Growth Tracking Reminders**: Helps parents monitor child's development

## Getting Started

### Prerequisites
- Android Studio
- Android SDK (Level 24+)
- Kotlin

### Installation
1. Clone the repository
```bash
git clone https://github.com/valalmas-15/ZeroStunt
```

2. Open the project in Android Studio
3. Sync Gradle files
4. Run the application

## Build Configuration
- Compile SDK: 34
- Target SDK: 34
- Min SDK: 24
- Version: 1.0

## Testing
- JUnit for unit testing
- Android Espresso for UI testing

## Contact
For more information, please contact the mobile development team.