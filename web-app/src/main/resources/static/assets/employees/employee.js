// Employee Overview JavaScript
document.addEventListener("DOMContentLoaded", function () {
  console.log("Employee overview page loaded");

  // Initialize search functionality
  initializeSearchFunctionality();

  // Export functionality
  initializeExportFunctionality();
});

function initializeSearchFunctionality() {
  const searchInput = document.getElementById("search-input");
  if (searchInput) {
    let searchTimeout;

    searchInput.addEventListener("input", function () {
      clearTimeout(searchTimeout);
      searchTimeout = setTimeout(() => {
        const searchTerm = this.value.trim();
        searchEmployees(searchTerm);
      }, 500);
    });
  }
}

function searchEmployees(searchTerm) {
  const url = new URL("/htmx/employees/search", window.location.origin);
  if (searchTerm) {
    url.searchParams.set("search", searchTerm);
  }

  // Use HTMX to update the table
  htmx.ajax("GET", url.toString(), {
    target: "#employee-table",
    swap: "innerHTML",
    indicator: "#loading-indicator",
  });
}

function initializeExportFunctionality() {
  // Add click handler to export button if it exists
  const exportButton = document.querySelector('a[href="/employees/export"]');
  if (exportButton) {
    exportButton.addEventListener("click", function (e) {
      e.preventDefault();
      exportEmployees();
    });
  }
}

function exportEmployees() {
  const searchInput = document.getElementById("search-input");
  const searchTerm =
    searchInput && searchInput.value ? searchInput.value.trim() : "";

  let url = "/employees/export";
  const params = new URLSearchParams();

  if (searchTerm) {
    params.set("search", searchTerm);
  }

  // Add current sorting parameters if available
  const urlParams = new URLSearchParams(window.location.search);
  const sortBy = urlParams.get("sortBy") || "name";
  const sortDir = urlParams.get("sortDir") || "asc";

  params.set("sortBy", sortBy);
  params.set("sortDir", sortDir);

  if (params.toString()) {
    url += "?" + params.toString();
  }

  // Trigger download
  window.open(url, "_blank");
}
