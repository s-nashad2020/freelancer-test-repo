// Employee Form JavaScript
document.addEventListener("DOMContentLoaded", function () {
  initializeFormValidation();
});

function initializeFormValidation() {
  const form = document.getElementById("employee-form");
  if (form) {
    form.addEventListener("submit", function (e) {
      if (!validateForm()) {
        e.preventDefault();
      }
    });
  }
}

function validateForm() {
  const nameField = document.getElementById("name");
  const emailField = document.getElementById("email");

  let isValid = true;

  // Clear previous errors
  clearFormErrors();

  // Validate required fields
  if (!nameField.value || !nameField.value.trim()) {
    showFieldError(nameField, "Name is required");
    isValid = false;
  }

  // Validate email format if provided
  if (
    emailField.value &&
    emailField.value.trim() &&
    !isValidEmail(emailField.value)
  ) {
    showFieldError(emailField, "Please enter a valid email address");
    isValid = false;
  }

  return isValid;
}

function isValidEmail(email) {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
}

function showFieldError(field, message) {
  field.setAttribute("aria-invalid", "true");

  const errorDiv = document.createElement("div");
  errorDiv.className = "error-message";
  errorDiv.textContent = message;

  field.parentNode.appendChild(errorDiv);
}

function clearFormErrors() {
  // Remove aria-invalid attributes
  document.querySelectorAll('[aria-invalid="true"]').forEach((field) => {
    field.removeAttribute("aria-invalid");
  });

  // Remove error messages
  document.querySelectorAll(".error-message").forEach((error) => {
    error.remove();
  });
}
