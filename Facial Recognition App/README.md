# Facial Recognition App (Java + SVM + HOG)

This project is a facial recognition system written in pure Java that identifies and classifies human faces using Support Vector Machines (SVM) and Histogram of Oriented Gradients (HOG) features.

### Key Features:
- Real-time face detection via webcam using OpenCV (for image capture only).
- SVM-based face recognition with per-person binary classifiers.
- Custom HOG feature extractor implemented from scratch.
- Fully implemented SMO (Sequential Minimal Optimization) algorithm with sigmoid kernel.
- Image acquisition system for building training datasets per person.
- Visual feedback: detected faces are highlighted in green and labeled with the predicted name.

### Training Mode:
The model must be trained **only once** at the beginning, using the **"Antrenează"** (Train) button. The trained SVM classifiers are then serialized to disk and automatically loaded in future runs — no need to retrain unless you modify the dataset.

### Technologies Used:
- Java (no IDEs, pure CLI tools)
- OpenCV (only for camera and drawing)
- SVM, SMO, HOG (custom implementation)
