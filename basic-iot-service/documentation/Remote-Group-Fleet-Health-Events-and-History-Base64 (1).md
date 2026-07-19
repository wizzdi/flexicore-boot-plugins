# Typed Remote and Fleet Health Architecture

> **Embedded-diagram edition:** Every diagram below is stored directly in this Markdown file as a Base64-encoded SVG data URI. The original Mermaid source is retained in a collapsible section beneath each image.


## Purpose

This document defines a rigid backend model for evaluating health across heterogeneous IoT remotes without relying on raw state-property names, without rescanning entire fleets on every update, and without requiring a general-purpose rules engine.

The design supports:

- Individual health evaluation for both `Device` and `Gateway`.
- Two or more device types with unrelated state schemas.
- Explicit `RemoteGroupToRemote` membership.
- Health aggregation for mixed `RemoteGroup` populations.
- Fleet-level health policies.
- Event-driven recalculation using Spring application events.
- Coalescing, hysteresis, and incremental counters to prevent event floods.
- Historical persistence of meaningful health transitions and periodic snapshots.
- Notifications to interested backend consumers, web clients, webhooks, email adapters, and other plugins.

---

## 1. Do severity rules rely on state names?

**They should not.**

A severity rule must not depend directly on a display name such as:

```text
temperature
doorState
battery
lastHeartbeat
```

Those names are schema-specific and can change between device types, firmware versions, vendors, or integrations.

The recommended chain is:

```text
Raw state value
    ↓
StatePropertyDefinition
    ↓
HealthSignalMapping
    ↓
Canonical HealthSignalDefinition
    ↓
RemoteHealthRule
    ↓
Normalized Remote health
    ↓
FleetHealthPolicy
    ↓
RemoteGroup health
```

Rules depend on a stable canonical signal identifier such as:

```text
TEMPERATURE_CELSIUS
BATTERY_PERCENT
CONNECTIVITY
LAST_SEEN_AGE_SECONDS
ACTUATOR_BLOCKED
SENSOR_DATA_STALE
HUMAN_INTERVENTION_REQUIRED
```

### Schema normalization flow

<p align="center">
  <img alt="Architecture flow diagram 1" src="data:image/svg+xml;base64,PCEtLSBHZW5lcmF0ZWQgYnkgZ3JhcGh2aXogdmVyc2lvbiAyLjQyLjQgKDApCiAtLT4KPCEtLSBUaXRsZTogRyBQYWdlczogMSAtLT4KPHN2ZyB3aWR0aD0iMTI0N3B0IiBoZWlnaHQ9IjI2MXB0Igogdmlld0JveD0iMC4wMCAwLjAwIDEyNDcuMDAgMjYxLjAwIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIj4KPGcgaWQ9ImdyYXBoMCIgY2xhc3M9ImdyYXBoIiB0cmFuc2Zvcm09InNjYWxlKDEgMSkgcm90YXRlKDApIHRyYW5zbGF0ZSgxOCAyNDMpIj4KPHRpdGxlPkc8L3RpdGxlPgo8IS0tIEExIC0tPgo8ZyBpZD0ibm9kZTEiIGNsYXNzPSJub2RlIj4KPHRpdGxlPkExPC90aXRsZT4KPHBhdGggZmlsbD0iI2Y4ZmFmYyIgc3Ryb2tlPSIjNGE1NTY4IiBzdHJva2Utd2lkdGg9IjEuMiIgZD0iTTEyNi41LC0yMjVDMTI2LjUsLTIyNSAzMS41LC0yMjUgMzEuNSwtMjI1IDI1LjUsLTIyNSAxOS41LC0yMTkgMTkuNSwtMjEzIDE5LjUsLTIxMyAxOS41LC0xOTkgMTkuNSwtMTk5IDE5LjUsLTE5MyAyNS41LC0xODcgMzEuNSwtMTg3IDMxLjUsLTE4NyAxMjYuNSwtMTg3IDEyNi41LC0xODcgMTMyLjUsLTE4NyAxMzguNSwtMTkzIDEzOC41LC0xOTkgMTM4LjUsLTE5OSAxMzguNSwtMjEzIDEzOC41LC0yMTMgMTM4LjUsLTIxOSAxMzIuNSwtMjI1IDEyNi41LC0yMjUiLz4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iNzkiIHk9Ii0yMDkuMiIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjExLjAwIiBmaWxsPSIjMWYyOTM3Ij5EZXZpY2UgdHlwZSBBIHN0YXRlPC90ZXh0Pgo8dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiB4PSI3OSIgeT0iLTE5Ny4yIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTEuMDAiIGZpbGw9IiMxZjI5MzciPmVudmlyb25tZW50LnRlbXBDPC90ZXh0Pgo8L2c+CjwhLS0gTTEgLS0+CjxnIGlkPSJub2RlMiIgY2xhc3M9Im5vZGUiPgo8dGl0bGU+TTE8L3RpdGxlPgo8cGF0aCBmaWxsPSIjZjhmYWZjIiBzdHJva2U9IiM0YTU1NjgiIHN0cm9rZS13aWR0aD0iMS4yIiBkPSJNMzE3LjUsLTIyM0MzMTcuNSwtMjIzIDIxNC41LC0yMjMgMjE0LjUsLTIyMyAyMDguNSwtMjIzIDIwMi41LC0yMTcgMjAyLjUsLTIxMSAyMDIuNSwtMjExIDIwMi41LC0xOTkgMjAyLjUsLTE5OSAyMDIuNSwtMTkzIDIwOC41LC0xODcgMjE0LjUsLTE4NyAyMTQuNSwtMTg3IDMxNy41LC0xODcgMzE3LjUsLTE4NyAzMjMuNSwtMTg3IDMyOS41LC0xOTMgMzI5LjUsLTE5OSAzMjkuNSwtMTk5IDMyOS41LC0yMTEgMzI5LjUsLTIxMSAzMjkuNSwtMjE3IDMyMy41LC0yMjMgMzE3LjUsLTIyMyIvPgo8dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiB4PSIyNjYiIHk9Ii0yMDIuMiIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjExLjAwIiBmaWxsPSIjMWYyOTM3Ij5IZWFsdGhTaWduYWxNYXBwaW5nPC90ZXh0Pgo8L2c+CjwhLS0gQTEmIzQ1OyZndDtNMSAtLT4KPGcgaWQ9ImVkZ2UxIiBjbGFzcz0iZWRnZSI+Cjx0aXRsZT5BMSYjNDU7Jmd0O00xPC90aXRsZT4KPHBhdGggZmlsbD0ibm9uZSIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgZD0iTTEzOC42OCwtMjA1QzEzOC42OCwtMjA1IDE5NC44NywtMjA1IDE5NC44NywtMjA1Ii8+Cjxwb2x5Z29uIGZpbGw9IiM2NDc0OGIiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIHBvaW50cz0iMTk0Ljg3LC0yMDcuNjMgMjAyLjM3LC0yMDUgMTk0Ljg3LC0yMDIuMzggMTk0Ljg3LC0yMDcuNjMiLz4KPC9nPgo8IS0tIFMxIC0tPgo8ZyBpZD0ibm9kZTciIGNsYXNzPSJub2RlIj4KPHRpdGxlPlMxPC90aXRsZT4KPHBhdGggZmlsbD0iI2Y4ZmFmYyIgc3Ryb2tlPSIjNGE1NTY4IiBzdHJva2Utd2lkdGg9IjEuMiIgZD0iTTUzMy41LC0xNzJDNTMzLjUsLTE3MiAzOTQuNSwtMTcyIDM5NC41LC0xNzIgMzg4LjUsLTE3MiAzODIuNSwtMTY2IDM4Mi41LC0xNjAgMzgyLjUsLTE2MCAzODIuNSwtMTQ4IDM4Mi41LC0xNDggMzgyLjUsLTE0MiAzODguNSwtMTM2IDM5NC41LC0xMzYgMzk0LjUsLTEzNiA1MzMuNSwtMTM2IDUzMy41LC0xMzYgNTM5LjUsLTEzNiA1NDUuNSwtMTQyIDU0NS41LC0xNDggNTQ1LjUsLTE0OCA1NDUuNSwtMTYwIDU0NS41LC0xNjAgNTQ1LjUsLTE2NiA1MzkuNSwtMTcyIDUzMy41LC0xNzIiLz4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iNDY0IiB5PSItMTUxLjIiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxMS4wMCIgZmlsbD0iIzFmMjkzNyI+VEVNUEVSQVRVUkVfQ0VMU0lVUzwvdGV4dD4KPC9nPgo8IS0tIE0xJiM0NTsmZ3Q7UzEgLS0+CjxnIGlkPSJlZGdlNCIgY2xhc3M9ImVkZ2UiPgo8dGl0bGU+TTEmIzQ1OyZndDtTMTwvdGl0bGU+CjxwYXRoIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIGQ9Ik0yNjYsLTE4Ni42OEMyNjYsLTE3NS41NiAyNjYsLTE2My41IDI2NiwtMTYzLjUgMjY2LC0xNjMuNSAzNzQuODIsLTE2My41IDM3NC44MiwtMTYzLjUiLz4KPHBvbHlnb24gZmlsbD0iIzY0NzQ4YiIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgcG9pbnRzPSIzNzQuODIsLTE2Ni4xMyAzODIuMzIsLTE2My41IDM3NC44MiwtMTYwLjg4IDM3NC44MiwtMTY2LjEzIi8+CjwvZz4KPCEtLSBBMiAtLT4KPGcgaWQ9Im5vZGUzIiBjbGFzcz0ibm9kZSI+Cjx0aXRsZT5BMjwvdGl0bGU+CjxwYXRoIGZpbGw9IiNmOGZhZmMiIHN0cm9rZT0iIzRhNTU2OCIgc3Ryb2tlLXdpZHRoPSIxLjIiIGQ9Ik0xNDYsLTE1NUMxNDYsLTE1NSAxMiwtMTU1IDEyLC0xNTUgNiwtMTU1IDAsLTE0OSAwLC0xNDMgMCwtMTQzIDAsLTEyOSAwLC0xMjkgMCwtMTIzIDYsLTExNyAxMiwtMTE3IDEyLC0xMTcgMTQ2LC0xMTcgMTQ2LC0xMTcgMTUyLC0xMTcgMTU4LC0xMjMgMTU4LC0xMjkgMTU4LC0xMjkgMTU4LC0xNDMgMTU4LC0xNDMgMTU4LC0xNDkgMTUyLC0xNTUgMTQ2LC0xNTUiLz4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iNzkiIHk9Ii0xMzkuMiIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjExLjAwIiBmaWxsPSIjMWYyOTM3Ij5EZXZpY2UgdHlwZSBCIHN0YXRlPC90ZXh0Pgo8dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiB4PSI3OSIgeT0iLTEyNy4yIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTEuMDAiIGZpbGw9IiMxZjI5MzciPnRlbGVtZXRyeS50ZW1wZXJhdHVyZS52YWx1ZTwvdGV4dD4KPC9nPgo8IS0tIE0yIC0tPgo8ZyBpZD0ibm9kZTQiIGNsYXNzPSJub2RlIj4KPHRpdGxlPk0yPC90aXRsZT4KPHBhdGggZmlsbD0iI2Y4ZmFmYyIgc3Ryb2tlPSIjNGE1NTY4IiBzdHJva2Utd2lkdGg9IjEuMiIgZD0iTTMxNy41LC0xNTVDMzE3LjUsLTE1NSAyMTQuNSwtMTU1IDIxNC41LC0xNTUgMjA4LjUsLTE1NSAyMDIuNSwtMTQ5IDIwMi41LC0xNDMgMjAyLjUsLTE0MyAyMDIuNSwtMTMxIDIwMi41LC0xMzEgMjAyLjUsLTEyNSAyMDguNSwtMTE5IDIxNC41LC0xMTkgMjE0LjUsLTExOSAzMTcuNSwtMTE5IDMxNy41LC0xMTkgMzIzLjUsLTExOSAzMjkuNSwtMTI1IDMyOS41LC0xMzEgMzI5LjUsLTEzMSAzMjkuNSwtMTQzIDMyOS41LC0xNDMgMzI5LjUsLTE0OSAzMjMuNSwtMTU1IDMxNy41LC0xNTUiLz4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iMjY2IiB5PSItMTM0LjIiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxMS4wMCIgZmlsbD0iIzFmMjkzNyI+SGVhbHRoU2lnbmFsTWFwcGluZzwvdGV4dD4KPC9nPgo8IS0tIEEyJiM0NTsmZ3Q7TTIgLS0+CjxnIGlkPSJlZGdlMiIgY2xhc3M9ImVkZ2UiPgo8dGl0bGU+QTImIzQ1OyZndDtNMjwvdGl0bGU+CjxwYXRoIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIGQ9Ik0xNTguMDMsLTEzN0MxNTguMDMsLTEzNyAxOTQuNzUsLTEzNyAxOTQuNzUsLTEzNyIvPgo8cG9seWdvbiBmaWxsPSIjNjQ3NDhiIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBwb2ludHM9IjE5NC43NSwtMTM5LjYzIDIwMi4yNSwtMTM3IDE5NC43NSwtMTM0LjM4IDE5NC43NSwtMTM5LjYzIi8+CjwvZz4KPCEtLSBNMiYjNDU7Jmd0O1MxIC0tPgo8ZyBpZD0iZWRnZTUiIGNsYXNzPSJlZGdlIj4KPHRpdGxlPk0yJiM0NTsmZ3Q7UzE8L3RpdGxlPgo8cGF0aCBmaWxsPSJub25lIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBkPSJNMzI5Ljc0LC0xNDUuNUMzMjkuNzQsLTE0NS41IDM3NC43OCwtMTQ1LjUgMzc0Ljc4LC0xNDUuNSIvPgo8cG9seWdvbiBmaWxsPSIjNjQ3NDhiIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBwb2ludHM9IjM3NC43OCwtMTQ4LjEzIDM4Mi4yOCwtMTQ1LjUgMzc0Ljc4LC0xNDIuODggMzc0Ljc4LC0xNDguMTMiLz4KPC9nPgo8IS0tIEcxIC0tPgo8ZyBpZD0ibm9kZTUiIGNsYXNzPSJub2RlIj4KPHRpdGxlPkcxPC90aXRsZT4KPHBhdGggZmlsbD0iI2Y4ZmFmYyIgc3Ryb2tlPSIjNGE1NTY4IiBzdHJva2Utd2lkdGg9IjEuMiIgZD0iTTEzMi41LC02OUMxMzIuNSwtNjkgMjUuNSwtNjkgMjUuNSwtNjkgMTkuNSwtNjkgMTMuNSwtNjMgMTMuNSwtNTcgMTMuNSwtNTcgMTMuNSwtNDMgMTMuNSwtNDMgMTMuNSwtMzcgMTkuNSwtMzEgMjUuNSwtMzEgMjUuNSwtMzEgMTMyLjUsLTMxIDEzMi41LC0zMSAxMzguNSwtMzEgMTQ0LjUsLTM3IDE0NC41LC00MyAxNDQuNSwtNDMgMTQ0LjUsLTU3IDE0NC41LC01NyAxNDQuNSwtNjMgMTM4LjUsLTY5IDEzMi41LC02OSIvPgo8dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiB4PSI3OSIgeT0iLTUzLjIiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxMS4wMCIgZmlsbD0iIzFmMjkzNyI+R2F0ZXdheSBtZXRhZGF0YTwvdGV4dD4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iNzkiIHk9Ii00MS4yIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTEuMDAiIGZpbGw9IiMxZjI5MzciPmxhc3RTZWVuIC8gY29ubmVjdGl2aXR5PC90ZXh0Pgo8L2c+CjwhLS0gQjEgLS0+CjxnIGlkPSJub2RlNiIgY2xhc3M9Im5vZGUiPgo8dGl0bGU+QjE8L3RpdGxlPgo8cGF0aCBmaWxsPSIjZjhmYWZjIiBzdHJva2U9IiM0YTU1NjgiIHN0cm9rZS13aWR0aD0iMS4yIiBkPSJNMzE5LC02OEMzMTksLTY4IDIxMywtNjggMjEzLC02OCAyMDcsLTY4IDIwMSwtNjIgMjAxLC01NiAyMDEsLTU2IDIwMSwtNDQgMjAxLC00NCAyMDEsLTM4IDIwNywtMzIgMjEzLC0zMiAyMTMsLTMyIDMxOSwtMzIgMzE5LC0zMiAzMjUsLTMyIDMzMSwtMzggMzMxLC00NCAzMzEsLTQ0IDMzMSwtNTYgMzMxLC01NiAzMzEsLTYyIDMyNSwtNjggMzE5LC02OCIvPgo8dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiB4PSIyNjYiIHk9Ii00Ny4yIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTEuMDAiIGZpbGw9IiMxZjI5MzciPkJ1aWx0JiM0NTtpbiBzaWduYWwgcmVzb2x2ZXI8L3RleHQ+CjwvZz4KPCEtLSBHMSYjNDU7Jmd0O0IxIC0tPgo8ZyBpZD0iZWRnZTMiIGNsYXNzPSJlZGdlIj4KPHRpdGxlPkcxJiM0NTsmZ3Q7QjE8L3RpdGxlPgo8cGF0aCBmaWxsPSJub25lIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBkPSJNMTQ0LjY3LC01MEMxNDQuNjcsLTUwIDE5My4zMSwtNTAgMTkzLjMxLC01MCIvPgo8cG9seWdvbiBmaWxsPSIjNjQ3NDhiIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBwb2ludHM9IjE5My4zMSwtNTIuNjMgMjAwLjgxLC01MCAxOTMuMzEsLTQ3LjM4IDE5My4zMSwtNTIuNjMiLz4KPC9nPgo8IS0tIFMyIC0tPgo8ZyBpZD0ibm9kZTgiIGNsYXNzPSJub2RlIj4KPHRpdGxlPlMyPC90aXRsZT4KPHBhdGggZmlsbD0iI2Y4ZmFmYyIgc3Ryb2tlPSIjNGE1NTY4IiBzdHJva2Utd2lkdGg9IjEuMiIgZD0iTTUwNiwtMTA0QzUwNiwtMTA0IDQyMiwtMTA0IDQyMiwtMTA0IDQxNiwtMTA0IDQxMCwtOTggNDEwLC05MiA0MTAsLTkyIDQxMCwtODAgNDEwLC04MCA0MTAsLTc0IDQxNiwtNjggNDIyLC02OCA0MjIsLTY4IDUwNiwtNjggNTA2LC02OCA1MTIsLTY4IDUxOCwtNzQgNTE4LC04MCA1MTgsLTgwIDUxOCwtOTIgNTE4LC05MiA1MTgsLTk4IDUxMiwtMTA0IDUwNiwtMTA0Ii8+Cjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIHg9IjQ2NCIgeT0iLTgzLjIiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxMS4wMCIgZmlsbD0iIzFmMjkzNyI+Q09OTkVDVElWSVRZPC90ZXh0Pgo8L2c+CjwhLS0gQjEmIzQ1OyZndDtTMiAtLT4KPGcgaWQ9ImVkZ2U2IiBjbGFzcz0iZWRnZSI+Cjx0aXRsZT5CMSYjNDU7Jmd0O1MyPC90aXRsZT4KPHBhdGggZmlsbD0ibm9uZSIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgZD0iTTMzMS4xMSwtNTcuMzNDMzg4LjgyLC01Ny4zMyA0NjQsLTU3LjMzIDQ2NCwtNTcuMzMgNDY0LC01Ny4zMyA0NjQsLTYwLjE4IDQ2NCwtNjAuMTgiLz4KPHBvbHlnb24gZmlsbD0iIzY0NzQ4YiIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgcG9pbnRzPSI0NjEuMzgsLTYwLjE4IDQ2NCwtNjcuNjggNDY2LjYzLC02MC4xOCA0NjEuMzgsLTYwLjE4Ii8+CjwvZz4KPCEtLSBTMyAtLT4KPGcgaWQ9Im5vZGU5IiBjbGFzcz0ibm9kZSI+Cjx0aXRsZT5TMzwvdGl0bGU+CjxwYXRoIGZpbGw9IiNmOGZhZmMiIHN0cm9rZT0iIzRhNTU2OCIgc3Ryb2tlLXdpZHRoPSIxLjIiIGQ9Ik01NDIsLTM2QzU0MiwtMzYgMzg2LC0zNiAzODYsLTM2IDM4MCwtMzYgMzc0LC0zMCAzNzQsLTI0IDM3NCwtMjQgMzc0LC0xMiAzNzQsLTEyIDM3NCwtNiAzODAsMCAzODYsMCAzODYsMCA1NDIsMCA1NDIsMCA1NDgsMCA1NTQsLTYgNTU0LC0xMiA1NTQsLTEyIDU1NCwtMjQgNTU0LC0yNCA1NTQsLTMwIDU0OCwtMzYgNTQyLC0zNiIvPgo8dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiB4PSI0NjQiIHk9Ii0xNS4yIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTEuMDAiIGZpbGw9IiMxZjI5MzciPkxBU1RfU0VFTl9BR0VfU0VDT05EUzwvdGV4dD4KPC9nPgo8IS0tIEIxJiM0NTsmZ3Q7UzMgLS0+CjxnIGlkPSJlZGdlNyIgY2xhc3M9ImVkZ2UiPgo8dGl0bGU+QjEmIzQ1OyZndDtTMzwvdGl0bGU+CjxwYXRoIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIGQ9Ik0zMzEuMDgsLTQ2LjY3QzM1Ni4yLC00Ni42NyAzNzksLTQ2LjY3IDM3OSwtNDYuNjcgMzc5LC00Ni42NyAzNzksLTQzLjgyIDM3OSwtNDMuODIiLz4KPHBvbHlnb24gZmlsbD0iIzY0NzQ4YiIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgcG9pbnRzPSIzODEuNjMsLTQzLjgyIDM3OSwtMzYuMzIgMzc2LjM4LC00My44MiAzODEuNjMsLTQzLjgyIi8+CjwvZz4KPCEtLSBQIC0tPgo8ZyBpZD0ibm9kZTEwIiBjbGFzcz0ibm9kZSI+Cjx0aXRsZT5QPC90aXRsZT4KPHBhdGggZmlsbD0iI2Y4ZmFmYyIgc3Ryb2tlPSIjNGE1NTY4IiBzdHJva2Utd2lkdGg9IjEuMiIgZD0iTTcxMCwtMTA0QzcxMCwtMTA0IDYwOSwtMTA0IDYwOSwtMTA0IDYwMywtMTA0IDU5NywtOTggNTk3LC05MiA1OTcsLTkyIDU5NywtODAgNTk3LC04MCA1OTcsLTc0IDYwMywtNjggNjA5LC02OCA2MDksLTY4IDcxMCwtNjggNzEwLC02OCA3MTYsLTY4IDcyMiwtNzQgNzIyLC04MCA3MjIsLTgwIDcyMiwtOTIgNzIyLC05MiA3MjIsLTk4IDcxNiwtMTA0IDcxMCwtMTA0Ii8+Cjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIHg9IjY1OS41IiB5PSItODMuMiIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjExLjAwIiBmaWxsPSIjMWYyOTM3Ij5SZW1vdGVIZWFsdGhQcm9maWxlPC90ZXh0Pgo8L2c+CjwhLS0gUzEmIzQ1OyZndDtQIC0tPgo8ZyBpZD0iZWRnZTgiIGNsYXNzPSJlZGdlIj4KPHRpdGxlPlMxJiM0NTsmZ3Q7UDwvdGl0bGU+CjxwYXRoIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIGQ9Ik01MzIsLTEzNS45OEM1MzIsLTExOC42MyA1MzIsLTk1IDUzMiwtOTUgNTMyLC05NSA1ODkuMzcsLTk1IDU4OS4zNywtOTUiLz4KPHBvbHlnb24gZmlsbD0iIzY0NzQ4YiIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgcG9pbnRzPSI1ODkuMzcsLTk3LjYzIDU5Ni44NywtOTUgNTg5LjM3LC05Mi4zOCA1ODkuMzcsLTk3LjYzIi8+CjwvZz4KPCEtLSBTMiYjNDU7Jmd0O1AgLS0+CjxnIGlkPSJlZGdlOSIgY2xhc3M9ImVkZ2UiPgo8dGl0bGU+UzImIzQ1OyZndDtQPC90aXRsZT4KPHBhdGggZmlsbD0ibm9uZSIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgZD0iTTUxOC4yLC04NkM1MTguMiwtODYgNTg5LjQsLTg2IDU4OS40LC04NiIvPgo8cG9seWdvbiBmaWxsPSIjNjQ3NDhiIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBwb2ludHM9IjU4OS40LC04OC42MyA1OTYuOSwtODYgNTg5LjQsLTgzLjM4IDU4OS40LC04OC42MyIvPgo8L2c+CjwhLS0gUzMmIzQ1OyZndDtQIC0tPgo8ZyBpZD0iZWRnZTEwIiBjbGFzcz0iZWRnZSI+Cjx0aXRsZT5TMyYjNDU7Jmd0O1A8L3RpdGxlPgo8cGF0aCBmaWxsPSJub25lIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBkPSJNNTUwLC0zNi4wMkM1NTAsLTUzLjM3IDU1MCwtNzcgNTUwLC03NyA1NTAsLTc3IDU4OS4yNiwtNzcgNTg5LjI2LC03NyIvPgo8cG9seWdvbiBmaWxsPSIjNjQ3NDhiIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBwb2ludHM9IjU4OS4yNiwtNzkuNjMgNTk2Ljc2LC03NyA1ODkuMjYsLTc0LjM4IDU4OS4yNiwtNzkuNjMiLz4KPC9nPgo8IS0tIFIgLS0+CjxnIGlkPSJub2RlMTEiIGNsYXNzPSJub2RlIj4KPHRpdGxlPlI8L3RpdGxlPgo8cGF0aCBmaWxsPSIjZjhmYWZjIiBzdHJva2U9IiM0YTU1NjgiIHN0cm9rZS13aWR0aD0iMS4yIiBkPSJNOTU0LC0xMDRDOTU0LC0xMDQgNzc3LC0xMDQgNzc3LC0xMDQgNzcxLC0xMDQgNzY1LC05OCA3NjUsLTkyIDc2NSwtOTIgNzY1LC04MCA3NjUsLTgwIDc2NSwtNzQgNzcxLC02OCA3NzcsLTY4IDc3NywtNjggOTU0LC02OCA5NTQsLTY4IDk2MCwtNjggOTY2LC03NCA5NjYsLTgwIDk2NiwtODAgOTY2LC05MiA5NjYsLTkyIDk2NiwtOTggOTYwLC0xMDQgOTU0LC0xMDQiLz4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iODY1LjUiIHk9Ii04My4yIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTEuMDAiIGZpbGw9IiMxZjI5MzciPlR5cGVkIFJlbW90ZUhlYWx0aFJ1bGUgZXZhbHVhdGlvbjwvdGV4dD4KPC9nPgo8IS0tIFAmIzQ1OyZndDtSIC0tPgo8ZyBpZD0iZWRnZTExIiBjbGFzcz0iZWRnZSI+Cjx0aXRsZT5QJiM0NTsmZ3Q7UjwvdGl0bGU+CjxwYXRoIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIGQ9Ik03MjIuMTUsLTg2QzcyMi4xNSwtODYgNzU3LjI2LC04NiA3NTcuMjYsLTg2Ii8+Cjxwb2x5Z29uIGZpbGw9IiM2NDc0OGIiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIHBvaW50cz0iNzU3LjI2LC04OC42MyA3NjQuNzYsLTg2IDc1Ny4yNiwtODMuMzggNzU3LjI2LC04OC42MyIvPgo8L2c+CjwhLS0gSCAtLT4KPGcgaWQ9Im5vZGUxMiIgY2xhc3M9Im5vZGUiPgo8dGl0bGU+SDwvdGl0bGU+CjxwYXRoIGZpbGw9IiNmOGZhZmMiIHN0cm9rZT0iIzRhNTU2OCIgc3Ryb2tlLXdpZHRoPSIxLjIiIGQ9Ik0xMTk5LC0xMDRDMTE5OSwtMTA0IDEwMjEsLTEwNCAxMDIxLC0xMDQgMTAxNSwtMTA0IDEwMDksLTk4IDEwMDksLTkyIDEwMDksLTkyIDEwMDksLTgwIDEwMDksLTgwIDEwMDksLTc0IDEwMTUsLTY4IDEwMjEsLTY4IDEwMjEsLTY4IDExOTksLTY4IDExOTksLTY4IDEyMDUsLTY4IDEyMTEsLTc0IDEyMTEsLTgwIDEyMTEsLTgwIDEyMTEsLTkyIDEyMTEsLTkyIDEyMTEsLTk4IDEyMDUsLTEwNCAxMTk5LC0xMDQiLz4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iMTExMCIgeT0iLTgzLjIiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxMS4wMCIgZmlsbD0iIzFmMjkzNyI+Tm9ybWFsaXplZCBSZW1vdGUgaGVhbHRoIHByb2plY3Rpb248L3RleHQ+CjwvZz4KPCEtLSBSJiM0NTsmZ3Q7SCAtLT4KPGcgaWQ9ImVkZ2UxMiIgY2xhc3M9ImVkZ2UiPgo8dGl0bGU+UiYjNDU7Jmd0O0g8L3RpdGxlPgo8cGF0aCBmaWxsPSJub25lIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBkPSJNOTY2LjAxLC04NkM5NjYuMDEsLTg2IDEwMDEuMiwtODYgMTAwMS4yLC04NiIvPgo8cG9seWdvbiBmaWxsPSIjNjQ3NDhiIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBwb2ludHM9IjEwMDEuMiwtODguNjMgMTAwOC43LC04NiAxMDAxLjIsLTgzLjM4IDEwMDEuMiwtODguNjMiLz4KPC9nPgo8L2c+Cjwvc3ZnPgo=" style="max-width: 100%; height: auto;" />
</p>

<details>
<summary>Mermaid source for diagram 1</summary>

```text
flowchart LR
    A1["Device type A state<br/>environment.tempC"] --> M1["HealthSignalMapping"]
    A2["Device type B state<br/>telemetry.temperature.value"] --> M2["HealthSignalMapping"]
    G1["Gateway metadata<br/>lastSeen / connectivity"] --> B1["Built-in signal resolver"]

    M1 --> S1["TEMPERATURE_CELSIUS"]
    M2 --> S1
    B1 --> S2["CONNECTIVITY"]
    B1 --> S3["LAST_SEEN_AGE_SECONDS"]

    S1 --> P["RemoteHealthProfile"]
    S2 --> P
    S3 --> P

    P --> R["Typed RemoteHealthRule evaluation"]
    R --> H["Normalized Remote health projection"]
```

</details>

Only the mapping layer knows that one device reports:

```text
environment.tempC
```

while another reports:

```text
telemetry.temperature.value
```

The health rule operates on `TEMPERATURE_CELSIUS`, not on either JSON path.

---

## 2. Core concepts

### 2.1 `Remote`

`Remote` is the common superclass for `Device` and `Gateway`.

Every remote has a normalized current-health projection:

```java
public abstract class Remote extends Baseclass {

    @ManyToOne(targetEntity = RemoteHealthProfile.class)
    private RemoteHealthProfile healthProfile;

    private String currentSeverityName;
    private Integer currentSeverityValue;

    @ManyToOne(targetEntity = RemoteHealthRule.class)
    private RemoteHealthRule currentSeverityRule;

    private OffsetDateTime severitySince;
    private OffsetDateTime healthCalculatedAt;

    private Boolean humanInterventionRequired;
    private String healthSummary;
    private String mitigationStatus;
    private String mitigationInstructions;

    private Long healthInputVersion;
    private String healthInputHash;
}
```

This projection allows dashboards and group aggregation to read the latest health without reevaluating raw state every time.

### 2.2 `StatePropertyDefinition`

A state property must have a stable entity identity independent of its display label.

```java
@Entity
public class StatePropertyDefinition extends Baseclass {

    @ManyToOne(targetEntity = StateSchema.class)
    private StateSchema stateSchema;

    private String canonicalPath;
    private String externalId;

    @Enumerated(EnumType.STRING)
    private StateValueType valueType;

    private String unit;
}
```

Examples:

```text
Device type A / schema v3 / property ID 91a... / environment.tempC
Device type B / schema v7 / property ID 5c4... / telemetry.temperature.value
```

Rules never store the display name. A mapping references the entity ID.

### 2.3 `HealthSignalDefinition`

A canonical health signal is a typed meaning shared by multiple device types.

```java
@Entity
public class HealthSignalDefinition extends Baseclass {

    private String externalId;

    @Enumerated(EnumType.STRING)
    private HealthSignalValueType valueType;

    private String unit;

    private Boolean builtIn;
    private Boolean aggregatable;
}
```

Examples:

| External ID | Type | Unit | Meaning |
|---|---:|---|---|
| `TEMPERATURE_CELSIUS` | NUMBER | °C | Normalized temperature |
| `BATTERY_PERCENT` | NUMBER | % | Battery state |
| `CONNECTIVITY` | ENUM | — | `ONLINE`, `OFFLINE`, `UNKNOWN` |
| `LAST_SEEN_AGE_SECONDS` | NUMBER | seconds | Time since last successful contact |
| `ACTUATOR_BLOCKED` | BOOLEAN | — | Mechanical movement is blocked |
| `HUMAN_INTERVENTION_REQUIRED` | BOOLEAN | — | Manual action is needed |

### 2.4 `HealthSignalMapping`

Mappings convert schema-specific data into canonical signals.

```java
@Entity
public class HealthSignalMapping extends Baseclass {

    @ManyToOne(targetEntity = DeviceType.class)
    private DeviceType deviceType;

    @ManyToOne(targetEntity = StateSchema.class)
    private StateSchema stateSchema;

    @ManyToOne(targetEntity = StatePropertyDefinition.class)
    private StatePropertyDefinition stateProperty;

    @ManyToOne(targetEntity = HealthSignalDefinition.class)
    private HealthSignalDefinition healthSignal;

    @Enumerated(EnumType.STRING)
    private SignalMappingOperation operation;

    private Double multiplier;
    private Double offset;
    private String expectedEnumValue;
    private Boolean negateBoolean;
    private Integer priority;
}
```

Typical mapping operations:

```java
public enum SignalMappingOperation {
    DIRECT,
    NUMERIC_SCALE,
    NUMERIC_OFFSET,
    ENUM_EQUALS,
    BOOLEAN_NEGATE,
    TIMESTAMP_AGE_SECONDS,
    EXISTS,
    MISSING,
    STRING_EQUALS
}
```

A mapping is version-aware because it points to a concrete `StateSchema` and property definition.

### 2.5 `RemoteHealthProfile`

A profile defines how an individual remote is classified.

```java
@Entity
public class RemoteHealthProfile extends Baseclass {

    @ManyToOne(targetEntity = SeverityDefinition.class)
    private SeverityDefinition defaultSeverity;

    private Integer evaluationVersion;
    private Boolean enabled;
}
```

A `DeviceType` can have a default profile. A specific `Remote` may override it.

Gateways can use a profile based entirely on built-in signals such as connectivity, last-seen age, process version, certificate expiry, or synchronization status.

### 2.6 `RemoteHealthRule`

Rules are typed entities, not free-form expressions.

```java
@Entity
public class RemoteHealthRule extends Baseclass {

    @ManyToOne(targetEntity = RemoteHealthProfile.class)
    private RemoteHealthProfile profile;

    @ManyToOne(targetEntity = SeverityDefinition.class)
    private SeverityDefinition resultingSeverity;

    private Integer priority;
    private Boolean enabled;

    private Long minimumStableMillis;
    private Long recoveryStableMillis;

    private Boolean humanInterventionRequired;
    private String summaryTemplate;
    private String mitigationInstructions;
}
```

Each rule has one or more typed conditions.

```java
@Entity
public class RemoteHealthCondition extends Baseclass {

    @ManyToOne(targetEntity = RemoteHealthRule.class)
    private RemoteHealthRule rule;

    @ManyToOne(targetEntity = HealthSignalDefinition.class)
    private HealthSignalDefinition signal;

    @Enumerated(EnumType.STRING)
    private HealthComparisonOperator operator;

    private Double numericValue;
    private String stringValue;
    private Boolean booleanValue;

    @Enumerated(EnumType.STRING)
    private MissingSignalBehavior missingSignalBehavior;

    private Integer priority;
}
```

Supported operators can remain rigid:

```java
public enum HealthComparisonOperator {
    EQ,
    NE,
    GT,
    GE,
    LT,
    LE,
    BETWEEN,
    IN,
    NOT_IN,
    IS_TRUE,
    IS_FALSE,
    IS_MISSING,
    IS_PRESENT
}
```

---

## 3. Example device type A: temperature sensor

### 3.1 Schema

```json
{
  "environment": {
    "tempC": 31.7,
    "humidityPct": 62.0
  },
  "power": {
    "batteryPct": 18
  },
  "communication": {
    "lastReport": "2026-07-13T06:15:00Z"
  }
}
```

Stable property definitions:

| Property entity | Path | Type |
|---|---|---|
| `temp-property-a` | `environment.tempC` | NUMBER |
| `battery-property-a` | `power.batteryPct` | NUMBER |
| `last-report-property-a` | `communication.lastReport` | TIMESTAMP |

Mappings:

| Source property | Canonical signal | Operation |
|---|---|---|
| `temp-property-a` | `TEMPERATURE_CELSIUS` | `DIRECT` |
| `battery-property-a` | `BATTERY_PERCENT` | `DIRECT` |
| `last-report-property-a` | `LAST_SEEN_AGE_SECONDS` | `TIMESTAMP_AGE_SECONDS` |

Profile rules:

| Priority | Conditions | Result |
|---:|---|---|
| 1000 | `LAST_SEEN_AGE_SECONDS >= 600` | Critical |
| 900 | `TEMPERATURE_CELSIUS >= 70` | Critical |
| 800 | `TEMPERATURE_CELSIUS >= 55` | Major |
| 700 | `BATTERY_PERCENT <= 10` | Major |
| 500 | `BATTERY_PERCENT <= 25` | Warning |
| 0 | default | Normal |

The health evaluator receives canonical values:

```json
{
  "TEMPERATURE_CELSIUS": 31.7,
  "BATTERY_PERCENT": 18,
  "LAST_SEEN_AGE_SECONDS": 45
}
```

It does not need to know the paths used by this device type.

Result:

```json
{
  "severityName": "Warning",
  "severityValue": 40,
  "matchedRule": "Low battery",
  "humanInterventionRequired": false,
  "summary": "Battery is below 25%"
}
```

---

## 4. Example device type B: parking barrier controller

### 4.1 Different schema

```json
{
  "controller": {
    "onlineStatus": "CONNECTED",
    "lastSeenEpochMs": 1783923300000
  },
  "barrier": {
    "position": "CLOSED",
    "movement": {
      "status": "JAMMED"
    }
  },
  "diagnostics": {
    "supplyVoltageMv": 10800
  }
}
```

Stable property definitions:

| Property entity | Path | Type |
|---|---|---|
| `online-property-b` | `controller.onlineStatus` | ENUM |
| `last-seen-property-b` | `controller.lastSeenEpochMs` | TIMESTAMP |
| `movement-property-b` | `barrier.movement.status` | ENUM |
| `voltage-property-b` | `diagnostics.supplyVoltageMv` | NUMBER |

Mappings:

| Source property | Canonical signal | Operation |
|---|---|---|
| `online-property-b` | `CONNECTIVITY` | enum mapping |
| `last-seen-property-b` | `LAST_SEEN_AGE_SECONDS` | `TIMESTAMP_AGE_SECONDS` |
| `movement-property-b` | `ACTUATOR_BLOCKED` | `ENUM_EQUALS("JAMMED")` |
| `voltage-property-b` | `SUPPLY_VOLTAGE_VOLTS` | `NUMERIC_SCALE(0.001)` |

Profile rules:

| Priority | Conditions | Result |
|---:|---|---|
| 1000 | `CONNECTIVITY == OFFLINE` | Critical |
| 950 | `ACTUATOR_BLOCKED == true` | Critical, intervention required |
| 800 | `LAST_SEEN_AGE_SECONDS >= 300` | Major |
| 700 | `SUPPLY_VOLTAGE_VOLTS < 11.0` | Warning |
| 0 | default | Normal |

Canonical values:

```json
{
  "CONNECTIVITY": "ONLINE",
  "LAST_SEEN_AGE_SECONDS": 25,
  "ACTUATOR_BLOCKED": true,
  "SUPPLY_VOLTAGE_VOLTS": 10.8
}
```

Result:

```json
{
  "severityName": "Critical",
  "severityValue": 100,
  "matchedRule": "Barrier movement blocked",
  "humanInterventionRequired": true,
  "summary": "Barrier is jammed",
  "mitigationInstructions": "Inspect the barrier arm and motor before retrying movement"
}
```

The temperature sensor and barrier controller share no raw schema names, but they still participate in one aggregate because both produce normalized severity and canonical health signals.

---

## 5. Gateway health

A gateway is also a `Remote`, even if it has no device state schema.

The backend provides built-in signal resolvers:

| Built-in signal | Source |
|---|---|
| `CONNECTIVITY` | session/connection state |
| `LAST_SEEN_AGE_SECONDS` | `Remote.lastSeen` |
| `REMOTE_VERSION` | gateway software metadata |
| `CERTIFICATE_EXPIRES_IN_DAYS` | certificate metadata |
| `SYNC_BACKLOG_COUNT` | synchronization subsystem |
| `CURRENT_SEVERITY_VALUE` | persisted remote health projection |
| `HUMAN_INTERVENTION_REQUIRED` | persisted remote health projection |

A gateway profile can therefore classify:

```text
Offline for 30 seconds         → Warning
Offline for 5 minutes          → Critical
Certificate expires in 7 days  → Warning
Sync backlog above 10,000       → Major
Unsupported software version    → Warning
```

Gateway health is normalized into the same remote projection used by devices.

---

## 6. RemoteGroup membership

### 6.1 Explicit entity

```java
@Entity
public class RemoteGroupToRemote extends Baseclass {

    @ManyToOne(targetEntity = RemoteGroup.class)
    private RemoteGroup remoteGroup;

    @ManyToOne(targetEntity = Remote.class)
    private Remote remote;

    @ManyToOne(targetEntity = RemoteRoleDefinition.class)
    private RemoteRoleDefinition role;

    @Enumerated(EnumType.STRING)
    private RemoteGroupMembershipAction action;

    private Boolean requiredMember;
    private Double weight;

    private OffsetDateTime activeFrom;
    private OffsetDateTime activeUntil;
}
```

This is preferred over a simple collection because membership itself has domain meaning.

It supports:

- Devices and gateways in the same group.
- Required infrastructure members.
- Role-specific aggregation.
- Weighted members.
- Explicit include/exclude semantics.
- Temporarily active members.
- Historical membership changes.

### 6.2 Example group

```text
Parking Site 14
 ├─ Gateway 14-A                 role=GATEWAY, required=true, weight=5
 ├─ Entry barrier               role=BARRIER, required=true, weight=3
 ├─ Exit barrier                role=BARRIER, required=true, weight=3
 ├─ Outdoor temperature sensor  role=ENVIRONMENT, required=false, weight=1
 └─ Equipment-room sensor       role=ENVIRONMENT, required=false, weight=1
```

The group is intentionally heterogeneous.

---

## 7. RemoteGroup health and fleet health

### 7.1 Terminology

The backend should distinguish three projections:

1. **Remote health**
   - One `Device` or `Gateway`.
   - Derived from canonical signals and a `RemoteHealthProfile`.

2. **RemoteGroup health**
   - One explicit `RemoteGroup`.
   - Derived from current member health and a `FleetHealthPolicy`.

3. **Fleet health**
   - The same aggregation mechanism applied to a fleet population.
   - A fleet may be represented by a `RemoteGroup`, a system-managed dynamic group, or a query-backed population.

The aggregation engine should not have separate logic for "group" and "fleet." It evaluates a population plus a typed policy.

### RemoteGroup aggregation flow

<p align="center">
  <img alt="Architecture flow diagram 2" src="data:image/svg+xml;base64,PCEtLSBHZW5lcmF0ZWQgYnkgZ3JhcGh2aXogdmVyc2lvbiAyLjQyLjQgKDApCiAtLT4KPCEtLSBUaXRsZTogRyBQYWdlczogMSAtLT4KPHN2ZyB3aWR0aD0iNzgxcHQiIGhlaWdodD0iNjI5cHQiCiB2aWV3Qm94PSIwLjAwIDAuMDAgNzgxLjAwIDYyOS4wMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayI+CjxnIGlkPSJncmFwaDAiIGNsYXNzPSJncmFwaCIgdHJhbnNmb3JtPSJzY2FsZSgxIDEpIHJvdGF0ZSgwKSB0cmFuc2xhdGUoMTggNjExKSI+Cjx0aXRsZT5HPC90aXRsZT4KPCEtLSBEMSAtLT4KPGcgaWQ9Im5vZGUxIiBjbGFzcz0ibm9kZSI+Cjx0aXRsZT5EMTwvdGl0bGU+CjxwYXRoIGZpbGw9IiNmOGZhZmMiIHN0cm9rZT0iIzRhNTU2OCIgc3Ryb2tlLXdpZHRoPSIxLjIiIGQ9Ik0yMzEsLTU5M0MyMzEsLTU5MyAxMzQsLTU5MyAxMzQsLTU5MyAxMjgsLTU5MyAxMjIsLTU4NyAxMjIsLTU4MSAxMjIsLTU4MSAxMjIsLTU2NyAxMjIsLTU2NyAxMjIsLTU2MSAxMjgsLTU1NSAxMzQsLTU1NSAxMzQsLTU1NSAyMzEsLTU1NSAyMzEsLTU1NSAyMzcsLTU1NSAyNDMsLTU2MSAyNDMsLTU2NyAyNDMsLTU2NyAyNDMsLTU4MSAyNDMsLTU4MSAyNDMsLTU4NyAyMzcsLTU5MyAyMzEsLTU5MyIvPgo8dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiB4PSIxODIuNSIgeT0iLTU3Ny4yIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTEuMDAiIGZpbGw9IiMxZjI5MzciPkRldmljZSBBPC90ZXh0Pgo8dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiB4PSIxODIuNSIgeT0iLTU2NS4yIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTEuMDAiIGZpbGw9IiMxZjI5MzciPlRlbXBlcmF0dXJlIHNlbnNvcjwvdGV4dD4KPC9nPgo8IS0tIFJIMSAtLT4KPGcgaWQ9Im5vZGUyIiBjbGFzcz0ibm9kZSI+Cjx0aXRsZT5SSDE8L3RpdGxlPgo8cGF0aCBmaWxsPSIjZjhmYWZjIiBzdHJva2U9IiM0YTU1NjgiIHN0cm9rZS13aWR0aD0iMS4yIiBkPSJNMjM4LC01MTJDMjM4LC01MTIgMTI3LC01MTIgMTI3LC01MTIgMTIxLC01MTIgMTE1LC01MDYgMTE1LC01MDAgMTE1LC01MDAgMTE1LC00ODggMTE1LC00ODggMTE1LC00ODIgMTIxLC00NzYgMTI3LC00NzYgMTI3LC00NzYgMjM4LC00NzYgMjM4LC00NzYgMjQ0LC00NzYgMjUwLC00ODIgMjUwLC00ODggMjUwLC00ODggMjUwLC01MDAgMjUwLC01MDAgMjUwLC01MDYgMjQ0LC01MTIgMjM4LC01MTIiLz4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iMTgyLjUiIHk9Ii00OTEuMiIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjExLjAwIiBmaWxsPSIjMWYyOTM3Ij5DdXJyZW50IFJlbW90ZSBoZWFsdGg8L3RleHQ+CjwvZz4KPCEtLSBEMSYjNDU7Jmd0O1JIMSAtLT4KPGcgaWQ9ImVkZ2UxIiBjbGFzcz0iZWRnZSI+Cjx0aXRsZT5EMSYjNDU7Jmd0O1JIMTwvdGl0bGU+CjxwYXRoIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIGQ9Ik0xODIuNSwtNTU0LjYzQzE4Mi41LC01NTQuNjMgMTgyLjUsLTUxOS41OSAxODIuNSwtNTE5LjU5Ii8+Cjxwb2x5Z29uIGZpbGw9IiM2NDc0OGIiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIHBvaW50cz0iMTg1LjEzLC01MTkuNTkgMTgyLjUsLTUxMi4wOSAxNzkuODgsLTUxOS41OSAxODUuMTMsLTUxOS41OSIvPgo8L2c+CjwhLS0gTTEgLS0+CjxnIGlkPSJub2RlNyIgY2xhc3M9Im5vZGUiPgo8dGl0bGU+TTE8L3RpdGxlPgo8cGF0aCBmaWxsPSIjZjhmYWZjIiBzdHJva2U9IiM0YTU1NjgiIHN0cm9rZS13aWR0aD0iMS4yIiBkPSJNMjQxLjUsLTQzM0MyNDEuNSwtNDMzIDEyMy41LC00MzMgMTIzLjUsLTQzMyAxMTcuNSwtNDMzIDExMS41LC00MjcgMTExLjUsLTQyMSAxMTEuNSwtNDIxIDExMS41LC00MDcgMTExLjUsLTQwNyAxMTEuNSwtNDAxIDExNy41LC0zOTUgMTIzLjUsLTM5NSAxMjMuNSwtMzk1IDI0MS41LC0zOTUgMjQxLjUsLTM5NSAyNDcuNSwtMzk1IDI1My41LC00MDEgMjUzLjUsLTQwNyAyNTMuNSwtNDA3IDI1My41LC00MjEgMjUzLjUsLTQyMSAyNTMuNSwtNDI3IDI0Ny41LC00MzMgMjQxLjUsLTQzMyIvPgo8dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiB4PSIxODIuNSIgeT0iLTQxNy4yIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTEuMDAiIGZpbGw9IiMxZjI5MzciPlJlbW90ZUdyb3VwVG9SZW1vdGU8L3RleHQ+Cjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIHg9IjE4Mi41IiB5PSItNDA1LjIiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxMS4wMCIgZmlsbD0iIzFmMjkzNyI+cm9sZSAvIHdlaWdodCAvIHJlcXVpcmVkPC90ZXh0Pgo8L2c+CjwhLS0gUkgxJiM0NTsmZ3Q7TTEgLS0+CjxnIGlkPSJlZGdlNCIgY2xhc3M9ImVkZ2UiPgo8dGl0bGU+UkgxJiM0NTsmZ3Q7TTE8L3RpdGxlPgo8cGF0aCBmaWxsPSJub25lIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBkPSJNMTgyLjUsLTQ3NS44NUMxODIuNSwtNDc1Ljg1IDE4Mi41LC00NDAuNyAxODIuNSwtNDQwLjciLz4KPHBvbHlnb24gZmlsbD0iIzY0NzQ4YiIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgcG9pbnRzPSIxODUuMTMsLTQ0MC43IDE4Mi41LC00MzMuMiAxNzkuODgsLTQ0MC43IDE4NS4xMywtNDQwLjciLz4KPC9nPgo8IS0tIEQyIC0tPgo8ZyBpZD0ibm9kZTMiIGNsYXNzPSJub2RlIj4KPHRpdGxlPkQyPC90aXRsZT4KPHBhdGggZmlsbD0iI2Y4ZmFmYyIgc3Ryb2tlPSIjNGE1NTY4IiBzdHJva2Utd2lkdGg9IjEuMiIgZD0iTTM5NywtNTkzQzM5NywtNTkzIDMxNiwtNTkzIDMxNiwtNTkzIDMxMCwtNTkzIDMwNCwtNTg3IDMwNCwtNTgxIDMwNCwtNTgxIDMwNCwtNTY3IDMwNCwtNTY3IDMwNCwtNTYxIDMxMCwtNTU1IDMxNiwtNTU1IDMxNiwtNTU1IDM5NywtNTU1IDM5NywtNTU1IDQwMywtNTU1IDQwOSwtNTYxIDQwOSwtNTY3IDQwOSwtNTY3IDQwOSwtNTgxIDQwOSwtNTgxIDQwOSwtNTg3IDQwMywtNTkzIDM5NywtNTkzIi8+Cjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIHg9IjM1Ni41IiB5PSItNTc3LjIiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxMS4wMCIgZmlsbD0iIzFmMjkzNyI+RGV2aWNlIEI8L3RleHQ+Cjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIHg9IjM1Ni41IiB5PSItNTY1LjIiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxMS4wMCIgZmlsbD0iIzFmMjkzNyI+QmFycmllciBjb250cm9sbGVyPC90ZXh0Pgo8L2c+CjwhLS0gUkgyIC0tPgo8ZyBpZD0ibm9kZTQiIGNsYXNzPSJub2RlIj4KPHRpdGxlPlJIMjwvdGl0bGU+CjxwYXRoIGZpbGw9IiNmOGZhZmMiIHN0cm9rZT0iIzRhNTU2OCIgc3Ryb2tlLXdpZHRoPSIxLjIiIGQ9Ik00MTIsLTUxMkM0MTIsLTUxMiAzMDEsLTUxMiAzMDEsLTUxMiAyOTUsLTUxMiAyODksLTUwNiAyODksLTUwMCAyODksLTUwMCAyODksLTQ4OCAyODksLTQ4OCAyODksLTQ4MiAyOTUsLTQ3NiAzMDEsLTQ3NiAzMDEsLTQ3NiA0MTIsLTQ3NiA0MTIsLTQ3NiA0MTgsLTQ3NiA0MjQsLTQ4MiA0MjQsLTQ4OCA0MjQsLTQ4OCA0MjQsLTUwMCA0MjQsLTUwMCA0MjQsLTUwNiA0MTgsLTUxMiA0MTIsLTUxMiIvPgo8dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiB4PSIzNTYuNSIgeT0iLTQ5MS4yIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTEuMDAiIGZpbGw9IiMxZjI5MzciPkN1cnJlbnQgUmVtb3RlIGhlYWx0aDwvdGV4dD4KPC9nPgo8IS0tIEQyJiM0NTsmZ3Q7UkgyIC0tPgo8ZyBpZD0iZWRnZTIiIGNsYXNzPSJlZGdlIj4KPHRpdGxlPkQyJiM0NTsmZ3Q7UkgyPC90aXRsZT4KPHBhdGggZmlsbD0ibm9uZSIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgZD0iTTM1Ni41LC01NTQuNjNDMzU2LjUsLTU1NC42MyAzNTYuNSwtNTE5LjU5IDM1Ni41LC01MTkuNTkiLz4KPHBvbHlnb24gZmlsbD0iIzY0NzQ4YiIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgcG9pbnRzPSIzNTkuMTMsLTUxOS41OSAzNTYuNSwtNTEyLjA5IDM1My44OCwtNTE5LjU5IDM1OS4xMywtNTE5LjU5Ii8+CjwvZz4KPCEtLSBNMiAtLT4KPGcgaWQ9Im5vZGU4IiBjbGFzcz0ibm9kZSI+Cjx0aXRsZT5NMjwvdGl0bGU+CjxwYXRoIGZpbGw9IiNmOGZhZmMiIHN0cm9rZT0iIzRhNTU2OCIgc3Ryb2tlLXdpZHRoPSIxLjIiIGQ9Ik00MTUuNSwtNDMzQzQxNS41LC00MzMgMjk3LjUsLTQzMyAyOTcuNSwtNDMzIDI5MS41LC00MzMgMjg1LjUsLTQyNyAyODUuNSwtNDIxIDI4NS41LC00MjEgMjg1LjUsLTQwNyAyODUuNSwtNDA3IDI4NS41LC00MDEgMjkxLjUsLTM5NSAyOTcuNSwtMzk1IDI5Ny41LC0zOTUgNDE1LjUsLTM5NSA0MTUuNSwtMzk1IDQyMS41LC0zOTUgNDI3LjUsLTQwMSA0MjcuNSwtNDA3IDQyNy41LC00MDcgNDI3LjUsLTQyMSA0MjcuNSwtNDIxIDQyNy41LC00MjcgNDIxLjUsLTQzMyA0MTUuNSwtNDMzIi8+Cjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIHg9IjM1Ni41IiB5PSItNDE3LjIiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxMS4wMCIgZmlsbD0iIzFmMjkzNyI+UmVtb3RlR3JvdXBUb1JlbW90ZTwvdGV4dD4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iMzU2LjUiIHk9Ii00MDUuMiIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjExLjAwIiBmaWxsPSIjMWYyOTM3Ij5yb2xlIC8gd2VpZ2h0IC8gcmVxdWlyZWQ8L3RleHQ+CjwvZz4KPCEtLSBSSDImIzQ1OyZndDtNMiAtLT4KPGcgaWQ9ImVkZ2U1IiBjbGFzcz0iZWRnZSI+Cjx0aXRsZT5SSDImIzQ1OyZndDtNMjwvdGl0bGU+CjxwYXRoIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIGQ9Ik0zNTYuNSwtNDc1Ljg1QzM1Ni41LC00NzUuODUgMzU2LjUsLTQ0MC43IDM1Ni41LC00NDAuNyIvPgo8cG9seWdvbiBmaWxsPSIjNjQ3NDhiIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBwb2ludHM9IjM1OS4xMywtNDQwLjcgMzU2LjUsLTQzMy4yIDM1My44OCwtNDQwLjcgMzU5LjEzLC00NDAuNyIvPgo8L2c+CjwhLS0gRzEgLS0+CjxnIGlkPSJub2RlNSIgY2xhc3M9Im5vZGUiPgo8dGl0bGU+RzE8L3RpdGxlPgo8cGF0aCBmaWxsPSIjZjhmYWZjIiBzdHJva2U9IiM0YTU1NjgiIHN0cm9rZS13aWR0aD0iMS4yIiBkPSJNNTUyLC01OTJDNTUyLC01OTIgNTA5LC01OTIgNTA5LC01OTIgNTAzLC01OTIgNDk3LC01ODYgNDk3LC01ODAgNDk3LC01ODAgNDk3LC01NjggNDk3LC01NjggNDk3LC01NjIgNTAzLC01NTYgNTA5LC01NTYgNTA5LC01NTYgNTUyLC01NTYgNTUyLC01NTYgNTU4LC01NTYgNTY0LC01NjIgNTY0LC01NjggNTY0LC01NjggNTY0LC01ODAgNTY0LC01ODAgNTY0LC01ODYgNTU4LC01OTIgNTUyLC01OTIiLz4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iNTMwLjUiIHk9Ii01NzEuMiIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjExLjAwIiBmaWxsPSIjMWYyOTM3Ij5HYXRld2F5PC90ZXh0Pgo8L2c+CjwhLS0gUkgzIC0tPgo8ZyBpZD0ibm9kZTYiIGNsYXNzPSJub2RlIj4KPHRpdGxlPlJIMzwvdGl0bGU+CjxwYXRoIGZpbGw9IiNmOGZhZmMiIHN0cm9rZT0iIzRhNTU2OCIgc3Ryb2tlLXdpZHRoPSIxLjIiIGQ9Ik01ODYsLTUxMkM1ODYsLTUxMiA0NzUsLTUxMiA0NzUsLTUxMiA0NjksLTUxMiA0NjMsLTUwNiA0NjMsLTUwMCA0NjMsLTUwMCA0NjMsLTQ4OCA0NjMsLTQ4OCA0NjMsLTQ4MiA0NjksLTQ3NiA0NzUsLTQ3NiA0NzUsLTQ3NiA1ODYsLTQ3NiA1ODYsLTQ3NiA1OTIsLTQ3NiA1OTgsLTQ4MiA1OTgsLTQ4OCA1OTgsLTQ4OCA1OTgsLTUwMCA1OTgsLTUwMCA1OTgsLTUwNiA1OTIsLTUxMiA1ODYsLTUxMiIvPgo8dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiB4PSI1MzAuNSIgeT0iLTQ5MS4yIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTEuMDAiIGZpbGw9IiMxZjI5MzciPkN1cnJlbnQgUmVtb3RlIGhlYWx0aDwvdGV4dD4KPC9nPgo8IS0tIEcxJiM0NTsmZ3Q7UkgzIC0tPgo8ZyBpZD0iZWRnZTMiIGNsYXNzPSJlZGdlIj4KPHRpdGxlPkcxJiM0NTsmZ3Q7UkgzPC90aXRsZT4KPHBhdGggZmlsbD0ibm9uZSIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgZD0iTTUzMC41LC01NTUuODVDNTMwLjUsLTU1NS44NSA1MzAuNSwtNTE5LjU4IDUzMC41LC01MTkuNTgiLz4KPHBvbHlnb24gZmlsbD0iIzY0NzQ4YiIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgcG9pbnRzPSI1MzMuMTMsLTUxOS41OCA1MzAuNSwtNTEyLjA4IDUyNy44OCwtNTE5LjU4IDUzMy4xMywtNTE5LjU4Ii8+CjwvZz4KPCEtLSBNMyAtLT4KPGcgaWQ9Im5vZGU5IiBjbGFzcz0ibm9kZSI+Cjx0aXRsZT5NMzwvdGl0bGU+CjxwYXRoIGZpbGw9IiNmOGZhZmMiIHN0cm9rZT0iIzRhNTU2OCIgc3Ryb2tlLXdpZHRoPSIxLjIiIGQ9Ik01ODkuNSwtNDMzQzU4OS41LC00MzMgNDcxLjUsLTQzMyA0NzEuNSwtNDMzIDQ2NS41LC00MzMgNDU5LjUsLTQyNyA0NTkuNSwtNDIxIDQ1OS41LC00MjEgNDU5LjUsLTQwNyA0NTkuNSwtNDA3IDQ1OS41LC00MDEgNDY1LjUsLTM5NSA0NzEuNSwtMzk1IDQ3MS41LC0zOTUgNTg5LjUsLTM5NSA1ODkuNSwtMzk1IDU5NS41LC0zOTUgNjAxLjUsLTQwMSA2MDEuNSwtNDA3IDYwMS41LC00MDcgNjAxLjUsLTQyMSA2MDEuNSwtNDIxIDYwMS41LC00MjcgNTk1LjUsLTQzMyA1ODkuNSwtNDMzIi8+Cjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIHg9IjUzMC41IiB5PSItNDE3LjIiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxMS4wMCIgZmlsbD0iIzFmMjkzNyI+UmVtb3RlR3JvdXBUb1JlbW90ZTwvdGV4dD4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iNTMwLjUiIHk9Ii00MDUuMiIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjExLjAwIiBmaWxsPSIjMWYyOTM3Ij5yb2xlIC8gd2VpZ2h0IC8gcmVxdWlyZWQ8L3RleHQ+CjwvZz4KPCEtLSBSSDMmIzQ1OyZndDtNMyAtLT4KPGcgaWQ9ImVkZ2U2IiBjbGFzcz0iZWRnZSI+Cjx0aXRsZT5SSDMmIzQ1OyZndDtNMzwvdGl0bGU+CjxwYXRoIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIGQ9Ik01MzAuNSwtNDc1Ljg1QzUzMC41LC00NzUuODUgNTMwLjUsLTQ0MC43IDUzMC41LC00NDAuNyIvPgo8cG9seWdvbiBmaWxsPSIjNjQ3NDhiIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBwb2ludHM9IjUzMy4xMywtNDQwLjcgNTMwLjUsLTQzMy4yIDUyNy44OCwtNDQwLjcgNTMzLjEzLC00NDAuNyIvPgo8L2c+CjwhLS0gQSAtLT4KPGcgaWQ9Im5vZGUxMCIgY2xhc3M9Im5vZGUiPgo8dGl0bGU+QTwvdGl0bGU+CjxwYXRoIGZpbGw9IiNmOGZhZmMiIHN0cm9rZT0iIzRhNTU2OCIgc3Ryb2tlLXdpZHRoPSIxLjIiIGQ9Ik00MTksLTM1MkM0MTksLTM1MiAyOTQsLTM1MiAyOTQsLTM1MiAyODgsLTM1MiAyODIsLTM0NiAyODIsLTM0MCAyODIsLTM0MCAyODIsLTMyOCAyODIsLTMyOCAyODIsLTMyMiAyODgsLTMxNiAyOTQsLTMxNiAyOTQsLTMxNiA0MTksLTMxNiA0MTksLTMxNiA0MjUsLTMxNiA0MzEsLTMyMiA0MzEsLTMyOCA0MzEsLTMyOCA0MzEsLTM0MCA0MzEsLTM0MCA0MzEsLTM0NiA0MjUsLTM1MiA0MTksLTM1MiIvPgo8dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiB4PSIzNTYuNSIgeT0iLTMzMS4yIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTEuMDAiIGZpbGw9IiMxZjI5MzciPkZsZWV0IGFnZ3JlZ2F0ZSBjYWxjdWxhdG9yPC90ZXh0Pgo8L2c+CjwhLS0gTTEmIzQ1OyZndDtBIC0tPgo8ZyBpZD0iZWRnZTciIGNsYXNzPSJlZGdlIj4KPHRpdGxlPk0xJiM0NTsmZ3Q7QTwvdGl0bGU+CjxwYXRoIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIGQ9Ik0xOTQuMjUsLTM5NC45N0MxOTQuMjUsLTM3My45NCAxOTQuMjUsLTM0MyAxOTQuMjUsLTM0MyAxOTQuMjUsLTM0MyAyNzQuMDUsLTM0MyAyNzQuMDUsLTM0MyIvPgo8cG9seWdvbiBmaWxsPSIjNjQ3NDhiIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBwb2ludHM9IjI3NC4wNSwtMzQ1LjYzIDI4MS41NSwtMzQzIDI3NC4wNSwtMzQwLjM4IDI3NC4wNSwtMzQ1LjYzIi8+CjwvZz4KPCEtLSBNMiYjNDU7Jmd0O0EgLS0+CjxnIGlkPSJlZGdlOCIgY2xhc3M9ImVkZ2UiPgo8dGl0bGU+TTImIzQ1OyZndDtBPC90aXRsZT4KPHBhdGggZmlsbD0ibm9uZSIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgZD0iTTM1Ni41LC0zOTQuNjNDMzU2LjUsLTM5NC42MyAzNTYuNSwtMzU5LjU5IDM1Ni41LC0zNTkuNTkiLz4KPHBvbHlnb24gZmlsbD0iIzY0NzQ4YiIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgcG9pbnRzPSIzNTkuMTMsLTM1OS41OSAzNTYuNSwtMzUyLjA5IDM1My44OCwtMzU5LjU5IDM1OS4xMywtMzU5LjU5Ii8+CjwvZz4KPCEtLSBNMyYjNDU7Jmd0O0EgLS0+CjxnIGlkPSJlZGdlOSIgY2xhc3M9ImVkZ2UiPgo8dGl0bGU+TTMmIzQ1OyZndDtBPC90aXRsZT4KPHBhdGggZmlsbD0ibm9uZSIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgZD0iTTUxMC41LC0zOTQuOTdDNTEwLjUsLTM3My45NCA1MTAuNSwtMzQzIDUxMC41LC0zNDMgNTEwLjUsLTM0MyA0MzguNzQsLTM0MyA0MzguNzQsLTM0MyIvPgo8cG9seWdvbiBmaWxsPSIjNjQ3NDhiIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBwb2ludHM9IjQzOC43NCwtMzQwLjM4IDQzMS4yNCwtMzQzIDQzOC43NCwtMzQ1LjYzIDQzOC43NCwtMzQwLjM4Ii8+CjwvZz4KPCEtLSBDMSAtLT4KPGcgaWQ9Im5vZGUxMSIgY2xhc3M9Im5vZGUiPgo8dGl0bGU+QzE8L3RpdGxlPgo8cGF0aCBmaWxsPSIjZjhmYWZjIiBzdHJva2U9IiM0YTU1NjgiIHN0cm9rZS13aWR0aD0iMS4yIiBkPSJNOTEsLTI3M0M5MSwtMjczIDEyLC0yNzMgMTIsLTI3MyA2LC0yNzMgMCwtMjY3IDAsLTI2MSAwLC0yNjEgMCwtMjQ5IDAsLTI0OSAwLC0yNDMgNiwtMjM3IDEyLC0yMzcgMTIsLTIzNyA5MSwtMjM3IDkxLC0yMzcgOTcsLTIzNyAxMDMsLTI0MyAxMDMsLTI0OSAxMDMsLTI0OSAxMDMsLTI2MSAxMDMsLTI2MSAxMDMsLTI2NyA5NywtMjczIDkxLC0yNzMiLz4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iNTEuNSIgeT0iLTI1Mi4yIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTEuMDAiIGZpbGw9IiMxZjI5MzciPlNldmVyaXR5IGJ1Y2tldHM8L3RleHQ+CjwvZz4KPCEtLSBBJiM0NTsmZ3Q7QzEgLS0+CjxnIGlkPSJlZGdlMTAiIGNsYXNzPSJlZGdlIj4KPHRpdGxlPkEmIzQ1OyZndDtDMTwvdGl0bGU+CjxwYXRoIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIGQ9Ik0yODEuODksLTMzNEMxOTEuNTksLTMzNCA1MS41LC0zMzQgNTEuNSwtMzM0IDUxLjUsLTMzNCA1MS41LC0yODAuODIgNTEuNSwtMjgwLjgyIi8+Cjxwb2x5Z29uIGZpbGw9IiM2NDc0OGIiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIHBvaW50cz0iNTQuMTMsLTI4MC44MiA1MS41LC0yNzMuMzIgNDguODgsLTI4MC44MiA1NC4xMywtMjgwLjgyIi8+CjwvZz4KPCEtLSBDMiAtLT4KPGcgaWQ9Im5vZGUxMiIgY2xhc3M9Im5vZGUiPgo8dGl0bGU+QzI8L3RpdGxlPgo8cGF0aCBmaWxsPSIjZjhmYWZjIiBzdHJva2U9IiM0YTU1NjgiIHN0cm9rZS13aWR0aD0iMS4yIiBkPSJNMjUwLC0yNzNDMjUwLC0yNzMgMTQ3LC0yNzMgMTQ3LC0yNzMgMTQxLC0yNzMgMTM1LC0yNjcgMTM1LC0yNjEgMTM1LC0yNjEgMTM1LC0yNDkgMTM1LC0yNDkgMTM1LC0yNDMgMTQxLC0yMzcgMTQ3LC0yMzcgMTQ3LC0yMzcgMjUwLC0yMzcgMjUwLC0yMzcgMjU2LC0yMzcgMjYyLC0yNDMgMjYyLC0yNDkgMjYyLC0yNDkgMjYyLC0yNjEgMjYyLC0yNjEgMjYyLC0yNjcgMjU2LC0yNzMgMjUwLC0yNzMiLz4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iMTk4LjUiIHk9Ii0yNTIuMiIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjExLjAwIiBmaWxsPSIjMWYyOTM3Ij5Db25uZWN0aXZpdHkgY291bnRlcnM8L3RleHQ+CjwvZz4KPCEtLSBBJiM0NTsmZ3Q7QzIgLS0+CjxnIGlkPSJlZGdlMTEiIGNsYXNzPSJlZGdlIj4KPHRpdGxlPkEmIzQ1OyZndDtDMjwvdGl0bGU+CjxwYXRoIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIGQ9Ik0yODEuOTEsLTMyNUMyNjguMDEsLTMyNSAyNTcuNzUsLTMyNSAyNTcuNzUsLTMyNSAyNTcuNzUsLTMyNSAyNTcuNzUsLTI4MC41MyAyNTcuNzUsLTI4MC41MyIvPgo8cG9seWdvbiBmaWxsPSIjNjQ3NDhiIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBwb2ludHM9IjI2MC4zOCwtMjgwLjUzIDI1Ny43NSwtMjczLjAzIDI1NS4xMywtMjgwLjUzIDI2MC4zOCwtMjgwLjUzIi8+CjwvZz4KPCEtLSBDMyAtLT4KPGcgaWQ9Im5vZGUxMyIgY2xhc3M9Im5vZGUiPgo8dGl0bGU+QzM8L3RpdGxlPgo8cGF0aCBmaWxsPSIjZjhmYWZjIiBzdHJva2U9IiM0YTU1NjgiIHN0cm9rZS13aWR0aD0iMS4yIiBkPSJNNDA3LC0yNzNDNDA3LC0yNzMgMzA2LC0yNzMgMzA2LC0yNzMgMzAwLC0yNzMgMjk0LC0yNjcgMjk0LC0yNjEgMjk0LC0yNjEgMjk0LC0yNDkgMjk0LC0yNDkgMjk0LC0yNDMgMzAwLC0yMzcgMzA2LC0yMzcgMzA2LC0yMzcgNDA3LC0yMzcgNDA3LC0yMzcgNDEzLC0yMzcgNDE5LC0yNDMgNDE5LC0yNDkgNDE5LC0yNDkgNDE5LC0yNjEgNDE5LC0yNjEgNDE5LC0yNjcgNDEzLC0yNzMgNDA3LC0yNzMiLz4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iMzU2LjUiIHk9Ii0yNTIuMiIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjExLjAwIiBmaWxsPSIjMWYyOTM3Ij5JbnRlcnZlbnRpb24gY291bnRlcnM8L3RleHQ+CjwvZz4KPCEtLSBBJiM0NTsmZ3Q7QzMgLS0+CjxnIGlkPSJlZGdlMTIiIGNsYXNzPSJlZGdlIj4KPHRpdGxlPkEmIzQ1OyZndDtDMzwvdGl0bGU+CjxwYXRoIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIGQ9Ik0zNTYuNSwtMzE1LjY4QzM1Ni41LC0zMTUuNjggMzU2LjUsLTI4MC43MiAzNTYuNSwtMjgwLjcyIi8+Cjxwb2x5Z29uIGZpbGw9IiM2NDc0OGIiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIHBvaW50cz0iMzU5LjEzLC0yODAuNzIgMzU2LjUsLTI3My4yMiAzNTMuODgsLTI4MC43MiAzNTkuMTMsLTI4MC43MiIvPgo8L2c+CjwhLS0gQzQgLS0+CjxnIGlkPSJub2RlMTQiIGNsYXNzPSJub2RlIj4KPHRpdGxlPkM0PC90aXRsZT4KPHBhdGggZmlsbD0iI2Y4ZmFmYyIgc3Ryb2tlPSIjNGE1NTY4IiBzdHJva2Utd2lkdGg9IjEuMiIgZD0iTTU0OS41LC0yNzNDNTQ5LjUsLTI3MyA0NjMuNSwtMjczIDQ2My41LC0yNzMgNDU3LjUsLTI3MyA0NTEuNSwtMjY3IDQ1MS41LC0yNjEgNDUxLjUsLTI2MSA0NTEuNSwtMjQ5IDQ1MS41LC0yNDkgNDUxLjUsLTI0MyA0NTcuNSwtMjM3IDQ2My41LC0yMzcgNDYzLjUsLTIzNyA1NDkuNSwtMjM3IDU0OS41LC0yMzcgNTU1LjUsLTIzNyA1NjEuNSwtMjQzIDU2MS41LC0yNDkgNTYxLjUsLTI0OSA1NjEuNSwtMjYxIDU2MS41LC0yNjEgNTYxLjUsLTI2NyA1NTUuNSwtMjczIDU0OS41LC0yNzMiLz4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iNTA2LjUiIHk9Ii0yNTIuMiIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjExLjAwIiBmaWxsPSIjMWYyOTM3Ij5XZWlnaHRlZCBzZXZlcml0eTwvdGV4dD4KPC9nPgo8IS0tIEEmIzQ1OyZndDtDNCAtLT4KPGcgaWQ9ImVkZ2UxMyIgY2xhc3M9ImVkZ2UiPgo8dGl0bGU+QSYjNDU7Jmd0O0M0PC90aXRsZT4KPHBhdGggZmlsbD0ibm9uZSIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgZD0iTTQzMS4wMywtMzI1QzQ0NS4wOSwtMzI1IDQ1NS41LC0zMjUgNDU1LjUsLTMyNSA0NTUuNSwtMzI1IDQ1NS41LC0yODAuNTMgNDU1LjUsLTI4MC41MyIvPgo8cG9seWdvbiBmaWxsPSIjNjQ3NDhiIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBwb2ludHM9IjQ1OC4xMywtMjgwLjUzIDQ1NS41LC0yNzMuMDMgNDUyLjg4LC0yODAuNTMgNDU4LjEzLC0yODAuNTMiLz4KPC9nPgo8IS0tIEM1IC0tPgo8ZyBpZD0ibm9kZTE1IiBjbGFzcz0ibm9kZSI+Cjx0aXRsZT5DNTwvdGl0bGU+CjxwYXRoIGZpbGw9IiNmOGZhZmMiIHN0cm9rZT0iIzRhNTU2OCIgc3Ryb2tlLXdpZHRoPSIxLjIiIGQ9Ik03MzMsLTI3M0M3MzMsLTI3MyA2MDYsLTI3MyA2MDYsLTI3MyA2MDAsLTI3MyA1OTQsLTI2NyA1OTQsLTI2MSA1OTQsLTI2MSA1OTQsLTI0OSA1OTQsLTI0OSA1OTQsLTI0MyA2MDAsLTIzNyA2MDYsLTIzNyA2MDYsLTIzNyA3MzMsLTIzNyA3MzMsLTIzNyA3MzksLTIzNyA3NDUsLTI0MyA3NDUsLTI0OSA3NDUsLTI0OSA3NDUsLTI2MSA3NDUsLTI2MSA3NDUsLTI2NyA3MzksLTI3MyA3MzMsLTI3MyIvPgo8dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiB4PSI2NjkuNSIgeT0iLTI1Mi4yIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTEuMDAiIGZpbGw9IiMxZjI5MzciPlNpZ25hbCYjNDU7c3BlY2lmaWMgcG9wdWxhdGlvbnM8L3RleHQ+CjwvZz4KPCEtLSBBJiM0NTsmZ3Q7QzUgLS0+CjxnIGlkPSJlZGdlMTQiIGNsYXNzPSJlZGdlIj4KPHRpdGxlPkEmIzQ1OyZndDtDNTwvdGl0bGU+CjxwYXRoIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIGQ9Ik00MzEuMTgsLTMzNEM1MDIuMDMsLTMzNCA1OTcuNzUsLTMzNCA1OTcuNzUsLTMzNCA1OTcuNzUsLTMzNCA1OTcuNzUsLTI4MC44MiA1OTcuNzUsLTI4MC44MiIvPgo8cG9seWdvbiBmaWxsPSIjNjQ3NDhiIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBwb2ludHM9IjYwMC4zOCwtMjgwLjgyIDU5Ny43NSwtMjczLjMyIDU5NS4xMywtMjgwLjgyIDYwMC4zOCwtMjgwLjgyIi8+CjwvZz4KPCEtLSBGUCAtLT4KPGcgaWQ9Im5vZGUxNiIgY2xhc3M9Im5vZGUiPgo8dGl0bGU+RlA8L3RpdGxlPgo8cGF0aCBmaWxsPSIjZjhmYWZjIiBzdHJva2U9IiM0YTU1NjgiIHN0cm9rZS13aWR0aD0iMS4yIiBkPSJNMzk5LC0xOTRDMzk5LC0xOTQgMzE0LC0xOTQgMzE0LC0xOTQgMzA4LC0xOTQgMzAyLC0xODggMzAyLC0xODIgMzAyLC0xODIgMzAyLC0xNzAgMzAyLC0xNzAgMzAyLC0xNjQgMzA4LC0xNTggMzE0LC0xNTggMzE0LC0xNTggMzk5LC0xNTggMzk5LC0xNTggNDA1LC0xNTggNDExLC0xNjQgNDExLC0xNzAgNDExLC0xNzAgNDExLC0xODIgNDExLC0xODIgNDExLC0xODggNDA1LC0xOTQgMzk5LC0xOTQiLz4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iMzU2LjUiIHk9Ii0xNzMuMiIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjExLjAwIiBmaWxsPSIjMWYyOTM3Ij5GbGVldEhlYWx0aFBvbGljeTwvdGV4dD4KPC9nPgo8IS0tIEMxJiM0NTsmZ3Q7RlAgLS0+CjxnIGlkPSJlZGdlMTUiIGNsYXNzPSJlZGdlIj4KPHRpdGxlPkMxJiM0NTsmZ3Q7RlA8L3RpdGxlPgo8cGF0aCBmaWxsPSJub25lIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBkPSJNNTEuNSwtMjM2Ljk3QzUxLjUsLTIxMi4wMiA1MS41LC0xNzAgNTEuNSwtMTcwIDUxLjUsLTE3MCAyOTQuNDcsLTE3MCAyOTQuNDcsLTE3MCIvPgo8cG9seWdvbiBmaWxsPSIjNjQ3NDhiIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBwb2ludHM9IjI5NC40NywtMTcyLjYzIDMwMS45NywtMTcwIDI5NC40NywtMTY3LjM4IDI5NC40NywtMTcyLjYzIi8+CjwvZz4KPCEtLSBDMiYjNDU7Jmd0O0ZQIC0tPgo8ZyBpZD0iZWRnZTE2IiBjbGFzcz0iZWRnZSI+Cjx0aXRsZT5DMiYjNDU7Jmd0O0ZQPC90aXRsZT4KPHBhdGggZmlsbD0ibm9uZSIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgZD0iTTE5OC41LC0yMzYuOTZDMTk4LjUsLTIxNS4zNCAxOTguNSwtMTgyIDE5OC41LC0xODIgMTk4LjUsLTE4MiAyOTQuMTgsLTE4MiAyOTQuMTgsLTE4MiIvPgo8cG9seWdvbiBmaWxsPSIjNjQ3NDhiIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBwb2ludHM9IjI5NC4xOCwtMTg0LjYzIDMwMS42OCwtMTgyIDI5NC4xOCwtMTc5LjM4IDI5NC4xOCwtMTg0LjYzIi8+CjwvZz4KPCEtLSBDMyYjNDU7Jmd0O0ZQIC0tPgo8ZyBpZD0iZWRnZTE3IiBjbGFzcz0iZWRnZSI+Cjx0aXRsZT5DMyYjNDU7Jmd0O0ZQPC90aXRsZT4KPHBhdGggZmlsbD0ibm9uZSIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgZD0iTTM1Ni41LC0yMzYuNjhDMzU2LjUsLTIzNi42OCAzNTYuNSwtMjAxLjcyIDM1Ni41LC0yMDEuNzIiLz4KPHBvbHlnb24gZmlsbD0iIzY0NzQ4YiIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgcG9pbnRzPSIzNTkuMTMsLTIwMS43MiAzNTYuNSwtMTk0LjIyIDM1My44OCwtMjAxLjcyIDM1OS4xMywtMjAxLjcyIi8+CjwvZz4KPCEtLSBDNCYjNDU7Jmd0O0ZQIC0tPgo8ZyBpZD0iZWRnZTE4IiBjbGFzcz0iZWRnZSI+Cjx0aXRsZT5DNCYjNDU7Jmd0O0ZQPC90aXRsZT4KPHBhdGggZmlsbD0ibm9uZSIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgZD0iTTUwNi41LC0yMzYuOTZDNTA2LjUsLTIxNS4zNCA1MDYuNSwtMTgyIDUwNi41LC0xODIgNTA2LjUsLTE4MiA0MTguNTgsLTE4MiA0MTguNTgsLTE4MiIvPgo8cG9seWdvbiBmaWxsPSIjNjQ3NDhiIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBwb2ludHM9IjQxOC41OCwtMTc5LjM4IDQxMS4wOCwtMTgyIDQxOC41OCwtMTg0LjYzIDQxOC41OCwtMTc5LjM4Ii8+CjwvZz4KPCEtLSBDNSYjNDU7Jmd0O0ZQIC0tPgo8ZyBpZD0iZWRnZTE5IiBjbGFzcz0iZWRnZSI+Cjx0aXRsZT5DNSYjNDU7Jmd0O0ZQPC90aXRsZT4KPHBhdGggZmlsbD0ibm9uZSIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgZD0iTTY2OS41LC0yMzYuOTdDNjY5LjUsLTIxMi4wMiA2NjkuNSwtMTcwIDY2OS41LC0xNzAgNjY5LjUsLTE3MCA0MTguNTIsLTE3MCA0MTguNTIsLTE3MCIvPgo8cG9seWdvbiBmaWxsPSIjNjQ3NDhiIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBwb2ludHM9IjQxOC41MiwtMTY3LjM4IDQxMS4wMiwtMTcwIDQxOC41MiwtMTcyLjYzIDQxOC41MiwtMTY3LjM4Ii8+CjwvZz4KPCEtLSBHSCAtLT4KPGcgaWQ9Im5vZGUxNyIgY2xhc3M9Im5vZGUiPgo8dGl0bGU+R0g8L3RpdGxlPgo8cGF0aCBmaWxsPSIjZjhmYWZjIiBzdHJva2U9IiM0YTU1NjgiIHN0cm9rZS13aWR0aD0iMS4yIiBkPSJNNDI1LjUsLTExNUM0MjUuNSwtMTE1IDI4Ny41LC0xMTUgMjg3LjUsLTExNSAyODEuNSwtMTE1IDI3NS41LC0xMDkgMjc1LjUsLTEwMyAyNzUuNSwtMTAzIDI3NS41LC05MSAyNzUuNSwtOTEgMjc1LjUsLTg1IDI4MS41LC03OSAyODcuNSwtNzkgMjg3LjUsLTc5IDQyNS41LC03OSA0MjUuNSwtNzkgNDMxLjUsLTc5IDQzNy41LC04NSA0MzcuNSwtOTEgNDM3LjUsLTkxIDQzNy41LC0xMDMgNDM3LjUsLTEwMyA0MzcuNSwtMTA5IDQzMS41LC0xMTUgNDI1LjUsLTExNSIvPgo8dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiB4PSIzNTYuNSIgeT0iLTk0LjIiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxMS4wMCIgZmlsbD0iIzFmMjkzNyI+UmVtb3RlR3JvdXAgY3VycmVudCBoZWFsdGg8L3RleHQ+CjwvZz4KPCEtLSBGUCYjNDU7Jmd0O0dIIC0tPgo8ZyBpZD0iZWRnZTIwIiBjbGFzcz0iZWRnZSI+Cjx0aXRsZT5GUCYjNDU7Jmd0O0dIPC90aXRsZT4KPHBhdGggZmlsbD0ibm9uZSIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgZD0iTTM1Ni41LC0xNTcuNjhDMzU2LjUsLTE1Ny42OCAzNTYuNSwtMTIyLjcyIDM1Ni41LC0xMjIuNzIiLz4KPHBvbHlnb24gZmlsbD0iIzY0NzQ4YiIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgcG9pbnRzPSIzNTkuMTMsLTEyMi43MiAzNTYuNSwtMTE1LjIyIDM1My44OCwtMTIyLjcyIDM1OS4xMywtMTIyLjcyIi8+CjwvZz4KPCEtLSBGSCAtLT4KPGcgaWQ9Im5vZGUxOCIgY2xhc3M9Im5vZGUiPgo8dGl0bGU+Rkg8L3RpdGxlPgo8cGF0aCBmaWxsPSIjZjhmYWZjIiBzdHJva2U9IiM0YTU1NjgiIHN0cm9rZS13aWR0aD0iMS4yIiBkPSJNNDM4LjUsLTM2QzQzOC41LC0zNiAyNzQuNSwtMzYgMjc0LjUsLTM2IDI2OC41LC0zNiAyNjIuNSwtMzAgMjYyLjUsLTI0IDI2Mi41LC0yNCAyNjIuNSwtMTIgMjYyLjUsLTEyIDI2Mi41LC02IDI2OC41LDAgMjc0LjUsMCAyNzQuNSwwIDQzOC41LDAgNDM4LjUsMCA0NDQuNSwwIDQ1MC41LC02IDQ1MC41LC0xMiA0NTAuNSwtMTIgNDUwLjUsLTI0IDQ1MC41LC0yNCA0NTAuNSwtMzAgNDQ0LjUsLTM2IDQzOC41LC0zNiIvPgo8dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiB4PSIzNTYuNSIgeT0iLTE1LjIiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxMS4wMCIgZmlsbD0iIzFmMjkzNyI+RmxlZXQgaGVhbHRoIHJlc3BvbnNlIC8gZGFzaGJvYXJkPC90ZXh0Pgo8L2c+CjwhLS0gR0gmIzQ1OyZndDtGSCAtLT4KPGcgaWQ9ImVkZ2UyMSIgY2xhc3M9ImVkZ2UiPgo8dGl0bGU+R0gmIzQ1OyZndDtGSDwvdGl0bGU+CjxwYXRoIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIGQ9Ik0zNTYuNSwtNzguNjhDMzU2LjUsLTc4LjY4IDM1Ni41LC00My43MiAzNTYuNSwtNDMuNzIiLz4KPHBvbHlnb24gZmlsbD0iIzY0NzQ4YiIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgcG9pbnRzPSIzNTkuMTMsLTQzLjcyIDM1Ni41LC0zNi4yMiAzNTMuODgsLTQzLjcyIDM1OS4xMywtNDMuNzIiLz4KPC9nPgo8L2c+Cjwvc3ZnPgo=" style="max-width: 100%; height: auto;" />
</p>

<details>
<summary>Mermaid source for diagram 2</summary>

```text
flowchart TB
    D1["Device A<br/>Temperature sensor"] --> RH1["Current Remote health"]
    D2["Device B<br/>Barrier controller"] --> RH2["Current Remote health"]
    G1["Gateway"] --> RH3["Current Remote health"]

    RH1 --> M1["RemoteGroupToRemote<br/>role / weight / required"]
    RH2 --> M2["RemoteGroupToRemote<br/>role / weight / required"]
    RH3 --> M3["RemoteGroupToRemote<br/>role / weight / required"]

    M1 --> A["Fleet aggregate calculator"]
    M2 --> A
    M3 --> A

    A --> C1["Severity buckets"]
    A --> C2["Connectivity counters"]
    A --> C3["Intervention counters"]
    A --> C4["Weighted severity"]
    A --> C5["Signal-specific populations"]

    C1 --> FP["FleetHealthPolicy"]
    C2 --> FP
    C3 --> FP
    C4 --> FP
    C5 --> FP

    FP --> GH["RemoteGroup current health"]
    GH --> FH["Fleet health response / dashboard"]
```

</details>

### 7.2 `FleetHealthPolicy`

```java
@Entity
public class FleetHealthPolicy extends Baseclass {

    @ManyToOne(targetEntity = SeverityDefinition.class)
    private SeverityDefinition defaultSeverity;

    private Boolean enabled;
    private Integer evaluationVersion;
}
```

### 7.3 Typed aggregate conditions

```java
@Entity
public class FleetHealthCondition extends Baseclass {

    @ManyToOne(targetEntity = FleetHealthRule.class)
    private FleetHealthRule rule;

    @Enumerated(EnumType.STRING)
    private FleetHealthMetric metric;

    @ManyToOne(targetEntity = SeverityDefinition.class)
    private SeverityDefinition severityThreshold;

    @ManyToOne(targetEntity = HealthSignalDefinition.class)
    private HealthSignalDefinition signal;

    @ManyToOne(targetEntity = RemoteRoleDefinition.class)
    private RemoteRoleDefinition role;

    @Enumerated(EnumType.STRING)
    private HealthComparisonOperator operator;

    private Double threshold;
    private Boolean requiredMembersOnly;

    @Enumerated(EnumType.STRING)
    private FleetDenominatorPolicy denominatorPolicy;

    @Enumerated(EnumType.STRING)
    private MissingSignalBehavior missingSignalBehavior;
}
```

Suggested aggregate metrics:

```java
public enum FleetHealthMetric {
    MEMBER_COUNT,
    ONLINE_COUNT,
    OFFLINE_COUNT,
    OFFLINE_PERCENT,
    STALE_COUNT,
    STALE_PERCENT,
    SEVERITY_AT_OR_ABOVE_COUNT,
    SEVERITY_AT_OR_ABOVE_PERCENT,
    WEIGHTED_SEVERITY_AVERAGE,
    REQUIRED_UNHEALTHY_COUNT,
    HUMAN_INTERVENTION_COUNT,
    HUMAN_INTERVENTION_PERCENT,
    SIGNAL_MATCH_COUNT,
    SIGNAL_MATCH_PERCENT,
    MISSING_SIGNAL_COUNT,
    MISSING_SIGNAL_PERCENT
}
```

### 7.4 Heterogeneous denominator handling

For a signal-specific metric, not every remote supports the signal.

Example: only temperature sensors expose `TEMPERATURE_CELSIUS`.

The condition must state its denominator policy:

```java
public enum FleetDenominatorPolicy {
    ALL_MEMBERS,
    MEMBERS_WITH_SIGNAL,
    MEMBERS_IN_ROLE,
    REQUIRED_MEMBERS,
    MATCHING_MEMBERS
}
```

For example:

```text
TEMPERATURE_CELSIUS >= 60 in 20% of MEMBERS_WITH_SIGNAL
```

This counts only members that support temperature.

By contrast:

```text
OFFLINE_PERCENT >= 20% of ALL_MEMBERS
```

applies to every remote because connectivity is a built-in signal.

### 7.5 Example group policy

| Priority | Condition | Result |
|---:|---|---|
| 1000 | Any required member severity ≥ Major | Critical |
| 950 | Human-intervention members ≥ 1 | Critical |
| 900 | Offline members ≥ 20% of all members | Major |
| 800 | Weighted severity average ≥ 50 | Major |
| 700 | Warning-or-higher members ≥ 25% | Warning |
| 500 | Temperature ≥ 60°C in 20% of members with temperature | Warning |
| 0 | default | Normal |

For the example group:

| Member | Severity | Value | Required | Weight |
|---|---|---:|---:|---:|
| Gateway 14-A | Normal | 0 | yes | 5 |
| Entry barrier | Critical | 100 | yes | 3 |
| Exit barrier | Normal | 0 | yes | 3 |
| Outdoor sensor | Warning | 40 | no | 1 |
| Equipment sensor | Normal | 0 | no | 1 |

The first matching rule is:

```text
Any required member severity >= Major
```

The resulting RemoteGroup health is `Critical`.

---

## 8. Event-driven evaluation

### Event propagation overview

<p align="center">
  <img alt="Architecture flow diagram 3" src="data:image/svg+xml;base64,PCEtLSBHZW5lcmF0ZWQgYnkgZ3JhcGh2aXogdmVyc2lvbiAyLjQyLjQgKDApCiAtLT4KPCEtLSBUaXRsZTogRyBQYWdlczogMSAtLT4KPHN2ZyB3aWR0aD0iMjk3M3B0IiBoZWlnaHQ9Ijc0cHQiCiB2aWV3Qm94PSIwLjAwIDAuMDAgMjk3My4wMCA3NC4wMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayI+CjxnIGlkPSJncmFwaDAiIGNsYXNzPSJncmFwaCIgdHJhbnNmb3JtPSJzY2FsZSgxIDEpIHJvdGF0ZSgwKSB0cmFuc2xhdGUoMTggNTYpIj4KPHRpdGxlPkc8L3RpdGxlPgo8IS0tIFUgLS0+CjxnIGlkPSJub2RlMSIgY2xhc3M9Im5vZGUiPgo8dGl0bGU+VTwvdGl0bGU+CjxwYXRoIGZpbGw9IiNmOGZhZmMiIHN0cm9rZT0iIzRhNTU2OCIgc3Ryb2tlLXdpZHRoPSIxLjIiIGQ9Ik0xODQsLTM3QzE4NCwtMzcgMTIsLTM3IDEyLC0zNyA2LC0zNyAwLC0zMSAwLC0yNSAwLC0yNSAwLC0xMyAwLC0xMyAwLC03IDYsLTEgMTIsLTEgMTIsLTEgMTg0LC0xIDE4NCwtMSAxOTAsLTEgMTk2LC03IDE5NiwtMTMgMTk2LC0xMyAxOTYsLTI1IDE5NiwtMjUgMTk2LC0zMSAxOTAsLTM3IDE4NCwtMzciLz4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iOTgiIHk9Ii0xNi4yIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTEuMDAiIGZpbGw9IiMxZjI5MzciPlJlbW90ZSBzdGF0ZSBvciBjb25uZWN0aXZpdHkgdXBkYXRlPC90ZXh0Pgo8L2c+CjwhLS0gQyAtLT4KPGcgaWQ9Im5vZGUyIiBjbGFzcz0ibm9kZSI+Cjx0aXRsZT5DPC90aXRsZT4KPHBhdGggZmlsbD0iI2Y4ZmFmYyIgc3Ryb2tlPSIjNGE1NTY4IiBzdHJva2Utd2lkdGg9IjEuMiIgZD0iTTM1MCwtMzdDMzUwLC0zNyAyNTEsLTM3IDI1MSwtMzcgMjQ1LC0zNyAyMzksLTMxIDIzOSwtMjUgMjM5LC0yNSAyMzksLTEzIDIzOSwtMTMgMjM5LC03IDI0NSwtMSAyNTEsLTEgMjUxLC0xIDM1MCwtMSAzNTAsLTEgMzU2LC0xIDM2MiwtNyAzNjIsLTEzIDM2MiwtMTMgMzYyLC0yNSAzNjIsLTI1IDM2MiwtMzEgMzU2LC0zNyAzNTAsLTM3Ii8+Cjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIHg9IjMwMC41IiB5PSItMTYuMiIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjExLjAwIiBmaWxsPSIjMWYyOTM3Ij5UcmFuc2FjdGlvbiBjb21taXRzPC90ZXh0Pgo8L2c+CjwhLS0gVSYjNDU7Jmd0O0MgLS0+CjxnIGlkPSJlZGdlMSIgY2xhc3M9ImVkZ2UiPgo8dGl0bGU+VSYjNDU7Jmd0O0M8L3RpdGxlPgo8cGF0aCBmaWxsPSJub25lIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBkPSJNMTk2LjI4LC0xOUMxOTYuMjgsLTE5IDIzMS4zNywtMTkgMjMxLjM3LC0xOSIvPgo8cG9seWdvbiBmaWxsPSIjNjQ3NDhiIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBwb2ludHM9IjIzMS4zNywtMjEuNjMgMjM4Ljg3LC0xOSAyMzEuMzcsLTE2LjM4IDIzMS4zNywtMjEuNjMiLz4KPC9nPgo8IS0tIEUxIC0tPgo8ZyBpZD0ibm9kZTMiIGNsYXNzPSJub2RlIj4KPHRpdGxlPkUxPC90aXRsZT4KPHBhdGggZmlsbD0iI2Y4ZmFmYyIgc3Ryb2tlPSIjNGE1NTY4IiBzdHJva2Utd2lkdGg9IjEuMiIgZD0iTTU1MywtMzhDNTUzLC0zOCA0MTcsLTM4IDQxNywtMzggNDExLC0zOCA0MDUsLTMyIDQwNSwtMjYgNDA1LC0yNiA0MDUsLTEyIDQwNSwtMTIgNDA1LC02IDQxMSwwIDQxNywwIDQxNywwIDU1MywwIDU1MywwIDU1OSwwIDU2NSwtNiA1NjUsLTEyIDU2NSwtMTIgNTY1LC0yNiA1NjUsLTI2IDU2NSwtMzIgNTU5LC0zOCA1NTMsLTM4Ii8+Cjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIHg9IjQ4NSIgeT0iLTIyLjIiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxMS4wMCIgZmlsbD0iIzFmMjkzNyI+UmVtb3RlU3RhdGVDaGFuZ2VkRXZlbnQ8L3RleHQ+Cjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIHg9IjQ4NSIgeT0iLTEwLjIiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxMS4wMCIgZmlsbD0iIzFmMjkzNyI+b3IgY29ubmVjdGl2aXR5IGV2ZW50PC90ZXh0Pgo8L2c+CjwhLS0gQyYjNDU7Jmd0O0UxIC0tPgo8ZyBpZD0iZWRnZTIiIGNsYXNzPSJlZGdlIj4KPHRpdGxlPkMmIzQ1OyZndDtFMTwvdGl0bGU+CjxwYXRoIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIGQ9Ik0zNjIuMTksLTE5QzM2Mi4xOSwtMTkgMzk3LjQ5LC0xOSAzOTcuNDksLTE5Ii8+Cjxwb2x5Z29uIGZpbGw9IiM2NDc0OGIiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIHBvaW50cz0iMzk3LjQ5LC0yMS42MyA0MDQuOTksLTE5IDM5Ny40OSwtMTYuMzggMzk3LjQ5LC0yMS42MyIvPgo8L2c+CjwhLS0gUkYgLS0+CjxnIGlkPSJub2RlNCIgY2xhc3M9Im5vZGUiPgo8dGl0bGU+UkY8L3RpdGxlPgo8cGF0aCBmaWxsPSIjZjhmYWZjIiBzdHJva2U9IiM0YTU1NjgiIHN0cm9rZS13aWR0aD0iMS4yIiBkPSJNNzMwLC0zN0M3MzAsLTM3IDYyMCwtMzcgNjIwLC0zNyA2MTQsLTM3IDYwOCwtMzEgNjA4LC0yNSA2MDgsLTI1IDYwOCwtMTMgNjA4LC0xMyA2MDgsLTcgNjE0LC0xIDYyMCwtMSA2MjAsLTEgNzMwLC0xIDczMCwtMSA3MzYsLTEgNzQyLC03IDc0MiwtMTMgNzQyLC0xMyA3NDIsLTI1IDc0MiwtMjUgNzQyLC0zMSA3MzYsLTM3IDczMCwtMzciLz4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iNjc1IiB5PSItMTYuMiIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjExLjAwIiBmaWxsPSIjMWYyOTM3Ij5SZWxldmFudCYjNDU7cHJvcGVydHkgZmlsdGVyPC90ZXh0Pgo8L2c+CjwhLS0gRTEmIzQ1OyZndDtSRiAtLT4KPGcgaWQ9ImVkZ2UzIiBjbGFzcz0iZWRnZSI+Cjx0aXRsZT5FMSYjNDU7Jmd0O1JGPC90aXRsZT4KPHBhdGggZmlsbD0ibm9uZSIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgZD0iTTU2NS4wMywtMTlDNTY1LjAzLC0xOSA2MDAuNDgsLTE5IDYwMC40OCwtMTkiLz4KPHBvbHlnb24gZmlsbD0iIzY0NzQ4YiIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgcG9pbnRzPSI2MDAuNDgsLTIxLjYzIDYwNy45OCwtMTkgNjAwLjQ4LC0xNi4zOCA2MDAuNDgsLTIxLjYzIi8+CjwvZz4KPCEtLSBRMSAtLT4KPGcgaWQ9Im5vZGU1IiBjbGFzcz0ibm9kZSI+Cjx0aXRsZT5RMTwvdGl0bGU+CjxwYXRoIGZpbGw9IiNmOGZhZmMiIHN0cm9rZT0iIzRhNTU2OCIgc3Ryb2tlLXdpZHRoPSIxLjIiIGQ9Ik05MzksLTM3QzkzOSwtMzcgNzk3LC0zNyA3OTcsLTM3IDc5MSwtMzcgNzg1LC0zMSA3ODUsLTI1IDc4NSwtMjUgNzg1LC0xMyA3ODUsLTEzIDc4NSwtNyA3OTEsLTEgNzk3LC0xIDc5NywtMSA5MzksLTEgOTM5LC0xIDk0NSwtMSA5NTEsLTcgOTUxLC0xMyA5NTEsLTEzIDk1MSwtMjUgOTUxLC0yNSA5NTEsLTMxIDk0NSwtMzcgOTM5LC0zNyIvPgo8dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiB4PSI4NjgiIHk9Ii0xNi4yIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTEuMDAiIGZpbGw9IiMxZjI5MzciPlBlciYjNDU7cmVtb3RlIGNvYWxlc2NpbmcgcXVldWU8L3RleHQ+CjwvZz4KPCEtLSBSRiYjNDU7Jmd0O1ExIC0tPgo8ZyBpZD0iZWRnZTQiIGNsYXNzPSJlZGdlIj4KPHRpdGxlPlJGJiM0NTsmZ3Q7UTE8L3RpdGxlPgo8cGF0aCBmaWxsPSJub25lIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBkPSJNNzQyLjIzLC0xOUM3NDIuMjMsLTE5IDc3Ny4yMSwtMTkgNzc3LjIxLC0xOSIvPgo8cG9seWdvbiBmaWxsPSIjNjQ3NDhiIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBwb2ludHM9Ijc3Ny4yMSwtMjEuNjMgNzg0LjcxLC0xOSA3NzcuMjEsLTE2LjM4IDc3Ny4yMSwtMjEuNjMiLz4KPC9nPgo8IS0tIFJFIC0tPgo8ZyBpZD0ibm9kZTYiIGNsYXNzPSJub2RlIj4KPHRpdGxlPlJFPC90aXRsZT4KPHBhdGggZmlsbD0iI2Y4ZmFmYyIgc3Ryb2tlPSIjNGE1NTY4IiBzdHJva2Utd2lkdGg9IjEuMiIgZD0iTTExMjksLTM3QzExMjksLTM3IDEwMDYsLTM3IDEwMDYsLTM3IDEwMDAsLTM3IDk5NCwtMzEgOTk0LC0yNSA5OTQsLTI1IDk5NCwtMTMgOTk0LC0xMyA5OTQsLTcgMTAwMCwtMSAxMDA2LC0xIDEwMDYsLTEgMTEyOSwtMSAxMTI5LC0xIDExMzUsLTEgMTE0MSwtNyAxMTQxLC0xMyAxMTQxLC0xMyAxMTQxLC0yNSAxMTQxLC0yNSAxMTQxLC0zMSAxMTM1LC0zNyAxMTI5LC0zNyIvPgo8dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiB4PSIxMDY3LjUiIHk9Ii0xNi4yIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTEuMDAiIGZpbGw9IiMxZjI5MzciPlJlbW90ZSBoZWFsdGggZXZhbHVhdGlvbjwvdGV4dD4KPC9nPgo8IS0tIFExJiM0NTsmZ3Q7UkUgLS0+CjxnIGlkPSJlZGdlNSIgY2xhc3M9ImVkZ2UiPgo8dGl0bGU+UTEmIzQ1OyZndDtSRTwvdGl0bGU+CjxwYXRoIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIGQ9Ik05NTEuMTYsLTE5Qzk1MS4xNiwtMTkgOTg2LjE5LC0xOSA5ODYuMTksLTE5Ii8+Cjxwb2x5Z29uIGZpbGw9IiM2NDc0OGIiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIHBvaW50cz0iOTg2LjE5LC0yMS42MyA5OTMuNjksLTE5IDk4Ni4xOSwtMTYuMzggOTg2LjE5LC0yMS42MyIvPgo8L2c+CjwhLS0gUlAgLS0+CjxnIGlkPSJub2RlNyIgY2xhc3M9Im5vZGUiPgo8dGl0bGU+UlA8L3RpdGxlPgo8cGF0aCBmaWxsPSIjZjhmYWZjIiBzdHJva2U9IiM0YTU1NjgiIHN0cm9rZS13aWR0aD0iMS4yIiBkPSJNMTM0MCwtMzdDMTM0MCwtMzcgMTE5NiwtMzcgMTE5NiwtMzcgMTE5MCwtMzcgMTE4NCwtMzEgMTE4NCwtMjUgMTE4NCwtMjUgMTE4NCwtMTMgMTE4NCwtMTMgMTE4NCwtNyAxMTkwLC0xIDExOTYsLTEgMTE5NiwtMSAxMzQwLC0xIDEzNDAsLTEgMTM0NiwtMSAxMzUyLC03IDEzNTIsLTEzIDEzNTIsLTEzIDEzNTIsLTI1IDEzNTIsLTI1IDEzNTIsLTMxIDEzNDYsLTM3IDEzNDAsLTM3Ii8+Cjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIHg9IjEyNjgiIHk9Ii0xNi4yIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTEuMDAiIGZpbGw9IiMxZjI5MzciPlBlcnNpc3QgY3VycmVudCBSZW1vdGUgaGVhbHRoPC90ZXh0Pgo8L2c+CjwhLS0gUkUmIzQ1OyZndDtSUCAtLT4KPGcgaWQ9ImVkZ2U2IiBjbGFzcz0iZWRnZSI+Cjx0aXRsZT5SRSYjNDU7Jmd0O1JQPC90aXRsZT4KPHBhdGggZmlsbD0ibm9uZSIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgZD0iTTExNDEuMDIsLTE5QzExNDEuMDIsLTE5IDExNzYuNDgsLTE5IDExNzYuNDgsLTE5Ii8+Cjxwb2x5Z29uIGZpbGw9IiM2NDc0OGIiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIHBvaW50cz0iMTE3Ni40OCwtMjEuNjMgMTE4My45OCwtMTkgMTE3Ni40OCwtMTYuMzggMTE3Ni40OCwtMjEuNjMiLz4KPC9nPgo8IS0tIEUyIC0tPgo8ZyBpZD0ibm9kZTgiIGNsYXNzPSJub2RlIj4KPHRpdGxlPkUyPC90aXRsZT4KPHBhdGggZmlsbD0iI2Y4ZmFmYyIgc3Ryb2tlPSIjNGE1NTY4IiBzdHJva2Utd2lkdGg9IjEuMiIgZD0iTTE1NDksLTM3QzE1NDksLTM3IDE0MDcsLTM3IDE0MDcsLTM3IDE0MDEsLTM3IDEzOTUsLTMxIDEzOTUsLTI1IDEzOTUsLTI1IDEzOTUsLTEzIDEzOTUsLTEzIDEzOTUsLTcgMTQwMSwtMSAxNDA3LC0xIDE0MDcsLTEgMTU0OSwtMSAxNTQ5LC0xIDE1NTUsLTEgMTU2MSwtNyAxNTYxLC0xMyAxNTYxLC0xMyAxNTYxLC0yNSAxNTYxLC0yNSAxNTYxLC0zMSAxNTU1LC0zNyAxNTQ5LC0zNyIvPgo8dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiB4PSIxNDc4IiB5PSItMTYuMiIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjExLjAwIiBmaWxsPSIjMWYyOTM3Ij5SZW1vdGVIZWFsdGhDaGFuZ2VkRXZlbnQ8L3RleHQ+CjwvZz4KPCEtLSBSUCYjNDU7Jmd0O0UyIC0tPgo8ZyBpZD0iZWRnZTciIGNsYXNzPSJlZGdlIj4KPHRpdGxlPlJQJiM0NTsmZ3Q7RTI8L3RpdGxlPgo8cGF0aCBmaWxsPSJub25lIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBkPSJNMTM1Mi4yMSwtMTlDMTM1Mi4yMSwtMTkgMTM4Ny4zLC0xOSAxMzg3LjMsLTE5Ii8+Cjxwb2x5Z29uIGZpbGw9IiM2NDc0OGIiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIHBvaW50cz0iMTM4Ny4zLC0yMS42MyAxMzk0LjgsLTE5IDEzODcuMywtMTYuMzggMTM4Ny4zLC0yMS42MyIvPgo8L2c+CjwhLS0gR00gLS0+CjxnIGlkPSJub2RlOSIgY2xhc3M9Im5vZGUiPgo8dGl0bGU+R008L3RpdGxlPgo8cGF0aCBmaWxsPSIjZjhmYWZjIiBzdHJva2U9IiM0YTU1NjgiIHN0cm9rZS13aWR0aD0iMS4yIiBkPSJNMTg0NCwtMzdDMTg0NCwtMzcgMTYxNiwtMzcgMTYxNiwtMzcgMTYxMCwtMzcgMTYwNCwtMzEgMTYwNCwtMjUgMTYwNCwtMjUgMTYwNCwtMTMgMTYwNCwtMTMgMTYwNCwtNyAxNjEwLC0xIDE2MTYsLTEgMTYxNiwtMSAxODQ0LC0xIDE4NDQsLTEgMTg1MCwtMSAxODU2LC03IDE4NTYsLTEzIDE4NTYsLTEzIDE4NTYsLTI1IDE4NTYsLTI1IDE4NTYsLTMxIDE4NTAsLTM3IDE4NDQsLTM3Ii8+Cjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIHg9IjE3MzAiIHk9Ii0xNi4yIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTEuMDAiIGZpbGw9IiMxZjI5MzciPlJlc29sdmUgUmVtb3RlR3JvdXBUb1JlbW90ZSBtZW1iZXJzaGlwczwvdGV4dD4KPC9nPgo8IS0tIEUyJiM0NTsmZ3Q7R00gLS0+CjxnIGlkPSJlZGdlOCIgY2xhc3M9ImVkZ2UiPgo8dGl0bGU+RTImIzQ1OyZndDtHTTwvdGl0bGU+CjxwYXRoIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIGQ9Ik0xNTYxLjIxLC0xOUMxNTYxLjIxLC0xOSAxNTk2LjA5LC0xOSAxNTk2LjA5LC0xOSIvPgo8cG9seWdvbiBmaWxsPSIjNjQ3NDhiIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBwb2ludHM9IjE1OTYuMDksLTIxLjYzIDE2MDMuNTksLTE5IDE1OTYuMDksLTE2LjM4IDE1OTYuMDksLTIxLjYzIi8+CjwvZz4KPCEtLSBRMiAtLT4KPGcgaWQ9Im5vZGUxMCIgY2xhc3M9Im5vZGUiPgo8dGl0bGU+UTI8L3RpdGxlPgo8cGF0aCBmaWxsPSIjZjhmYWZjIiBzdHJva2U9IiM0YTU1NjgiIHN0cm9rZS13aWR0aD0iMS4yIiBkPSJNMjA0NywtMzdDMjA0NywtMzcgMTkxMSwtMzcgMTkxMSwtMzcgMTkwNSwtMzcgMTg5OSwtMzEgMTg5OSwtMjUgMTg5OSwtMjUgMTg5OSwtMTMgMTg5OSwtMTMgMTg5OSwtNyAxOTA1LC0xIDE5MTEsLTEgMTkxMSwtMSAyMDQ3LC0xIDIwNDcsLTEgMjA1MywtMSAyMDU5LC03IDIwNTksLTEzIDIwNTksLTEzIDIwNTksLTI1IDIwNTksLTI1IDIwNTksLTMxIDIwNTMsLTM3IDIwNDcsLTM3Ii8+Cjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIHg9IjE5NzkiIHk9Ii0xNi4yIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTEuMDAiIGZpbGw9IiMxZjI5MzciPlBlciYjNDU7Z3JvdXAgY29hbGVzY2luZyBxdWV1ZTwvdGV4dD4KPC9nPgo8IS0tIEdNJiM0NTsmZ3Q7UTIgLS0+CjxnIGlkPSJlZGdlOSIgY2xhc3M9ImVkZ2UiPgo8dGl0bGU+R00mIzQ1OyZndDtRMjwvdGl0bGU+CjxwYXRoIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIGQ9Ik0xODU2LjMyLC0xOUMxODU2LjMyLC0xOSAxODkxLjI5LC0xOSAxODkxLjI5LC0xOSIvPgo8cG9seWdvbiBmaWxsPSIjNjQ3NDhiIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBwb2ludHM9IjE4OTEuMjksLTIxLjYzIDE4OTguNzksLTE5IDE4OTEuMjksLTE2LjM4IDE4OTEuMjksLTIxLjYzIi8+CjwvZz4KPCEtLSBHRSAtLT4KPGcgaWQ9Im5vZGUxMSIgY2xhc3M9Im5vZGUiPgo8dGl0bGU+R0U8L3RpdGxlPgo8cGF0aCBmaWxsPSIjZjhmYWZjIiBzdHJva2U9IiM0YTU1NjgiIHN0cm9rZS13aWR0aD0iMS4yIiBkPSJNMjI1MCwtMzdDMjI1MCwtMzcgMjExNCwtMzcgMjExNCwtMzcgMjEwOCwtMzcgMjEwMiwtMzEgMjEwMiwtMjUgMjEwMiwtMjUgMjEwMiwtMTMgMjEwMiwtMTMgMjEwMiwtNyAyMTA4LC0xIDIxMTQsLTEgMjExNCwtMSAyMjUwLC0xIDIyNTAsLTEgMjI1NiwtMSAyMjYyLC03IDIyNjIsLTEzIDIyNjIsLTEzIDIyNjIsLTI1IDIyNjIsLTI1IDIyNjIsLTMxIDIyNTYsLTM3IDIyNTAsLTM3Ii8+Cjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIHg9IjIxODIiIHk9Ii0xNi4yIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTEuMDAiIGZpbGw9IiMxZjI5MzciPkZsZWV0SGVhbHRoUG9saWN5IGV2YWx1YXRpb248L3RleHQ+CjwvZz4KPCEtLSBRMiYjNDU7Jmd0O0dFIC0tPgo8ZyBpZD0iZWRnZTEwIiBjbGFzcz0iZWRnZSI+Cjx0aXRsZT5RMiYjNDU7Jmd0O0dFPC90aXRsZT4KPHBhdGggZmlsbD0ibm9uZSIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgZD0iTTIwNTkuMjMsLTE5QzIwNTkuMjMsLTE5IDIwOTQuMTksLTE5IDIwOTQuMTksLTE5Ii8+Cjxwb2x5Z29uIGZpbGw9IiM2NDc0OGIiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIHBvaW50cz0iMjA5NC4xOSwtMjEuNjMgMjEwMS42OSwtMTkgMjA5NC4xOSwtMTYuMzggMjA5NC4xOSwtMjEuNjMiLz4KPC9nPgo8IS0tIEdQIC0tPgo8ZyBpZD0ibm9kZTEyIiBjbGFzcz0ibm9kZSI+Cjx0aXRsZT5HUDwvdGl0bGU+CjxwYXRoIGZpbGw9IiNmOGZhZmMiIHN0cm9rZT0iIzRhNTU2OCIgc3Ryb2tlLXdpZHRoPSIxLjIiIGQ9Ik0yNDU0LC0zN0MyNDU0LC0zNyAyMzE3LC0zNyAyMzE3LC0zNyAyMzExLC0zNyAyMzA1LC0zMSAyMzA1LC0yNSAyMzA1LC0yNSAyMzA1LC0xMyAyMzA1LC0xMyAyMzA1LC03IDIzMTEsLTEgMjMxNywtMSAyMzE3LC0xIDI0NTQsLTEgMjQ1NCwtMSAyNDYwLC0xIDI0NjYsLTcgMjQ2NiwtMTMgMjQ2NiwtMTMgMjQ2NiwtMjUgMjQ2NiwtMjUgMjQ2NiwtMzEgMjQ2MCwtMzcgMjQ1NCwtMzciLz4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iMjM4NS41IiB5PSItMTYuMiIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjExLjAwIiBmaWxsPSIjMWYyOTM3Ij5QZXJzaXN0IFJlbW90ZUdyb3VwIGhlYWx0aDwvdGV4dD4KPC9nPgo8IS0tIEdFJiM0NTsmZ3Q7R1AgLS0+CjxnIGlkPSJlZGdlMTEiIGNsYXNzPSJlZGdlIj4KPHRpdGxlPkdFJiM0NTsmZ3Q7R1A8L3RpdGxlPgo8cGF0aCBmaWxsPSJub25lIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBkPSJNMjI2Mi4xNCwtMTlDMjI2Mi4xNCwtMTkgMjI5Ny4yNywtMTkgMjI5Ny4yNywtMTkiLz4KPHBvbHlnb24gZmlsbD0iIzY0NzQ4YiIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgcG9pbnRzPSIyMjk3LjI3LC0yMS42MyAyMzA0Ljc3LC0xOSAyMjk3LjI3LC0xNi4zOCAyMjk3LjI3LC0yMS42MyIvPgo8L2c+CjwhLS0gRTMgLS0+CjxnIGlkPSJub2RlMTMiIGNsYXNzPSJub2RlIj4KPHRpdGxlPkUzPC90aXRsZT4KPHBhdGggZmlsbD0iI2Y4ZmFmYyIgc3Ryb2tlPSIjNGE1NTY4IiBzdHJva2Utd2lkdGg9IjEuMiIgZD0iTTI2OTMsLTM3QzI2OTMsLTM3IDI1MjEsLTM3IDI1MjEsLTM3IDI1MTUsLTM3IDI1MDksLTMxIDI1MDksLTI1IDI1MDksLTI1IDI1MDksLTEzIDI1MDksLTEzIDI1MDksLTcgMjUxNSwtMSAyNTIxLC0xIDI1MjEsLTEgMjY5MywtMSAyNjkzLC0xIDI2OTksLTEgMjcwNSwtNyAyNzA1LC0xMyAyNzA1LC0xMyAyNzA1LC0yNSAyNzA1LC0yNSAyNzA1LC0zMSAyNjk5LC0zNyAyNjkzLC0zNyIvPgo8dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiB4PSIyNjA3IiB5PSItMTYuMiIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjExLjAwIiBmaWxsPSIjMWYyOTM3Ij5SZW1vdGVHcm91cEhlYWx0aENoYW5nZWRFdmVudDwvdGV4dD4KPC9nPgo8IS0tIEdQJiM0NTsmZ3Q7RTMgLS0+CjxnIGlkPSJlZGdlMTIiIGNsYXNzPSJlZGdlIj4KPHRpdGxlPkdQJiM0NTsmZ3Q7RTM8L3RpdGxlPgo8cGF0aCBmaWxsPSJub25lIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBkPSJNMjQ2Ni4wOSwtMTlDMjQ2Ni4wOSwtMTkgMjUwMS4yNCwtMTkgMjUwMS4yNCwtMTkiLz4KPHBvbHlnb24gZmlsbD0iIzY0NzQ4YiIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgcG9pbnRzPSIyNTAxLjI0LC0yMS42MyAyNTA4Ljc0LC0xOSAyNTAxLjI0LC0xNi4zOCAyNTAxLjI0LC0yMS42MyIvPgo8L2c+CjwhLS0gTiAtLT4KPGcgaWQ9Im5vZGUxNCIgY2xhc3M9Im5vZGUiPgo8dGl0bGU+TjwvdGl0bGU+CjxwYXRoIGZpbGw9IiNmOGZhZmMiIHN0cm9rZT0iIzRhNTU2OCIgc3Ryb2tlLXdpZHRoPSIxLjIiIGQ9Ik0yOTI1LC0zN0MyOTI1LC0zNyAyNzYwLC0zNyAyNzYwLC0zNyAyNzU0LC0zNyAyNzQ4LC0zMSAyNzQ4LC0yNSAyNzQ4LC0yNSAyNzQ4LC0xMyAyNzQ4LC0xMyAyNzQ4LC03IDI3NTQsLTEgMjc2MCwtMSAyNzYwLC0xIDI5MjUsLTEgMjkyNSwtMSAyOTMxLC0xIDI5MzcsLTcgMjkzNywtMTMgMjkzNywtMTMgMjkzNywtMjUgMjkzNywtMjUgMjkzNywtMzEgMjkzMSwtMzcgMjkyNSwtMzciLz4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iMjg0Mi41IiB5PSItMTYuMiIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjExLjAwIiBmaWxsPSIjMWYyOTM3Ij5IaXN0b3J5IGFuZCBub3RpZmljYXRpb24gY29uc3VtZXJzPC90ZXh0Pgo8L2c+CjwhLS0gRTMmIzQ1OyZndDtOIC0tPgo8ZyBpZD0iZWRnZTEzIiBjbGFzcz0iZWRnZSI+Cjx0aXRsZT5FMyYjNDU7Jmd0O048L3RpdGxlPgo8cGF0aCBmaWxsPSJub25lIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBkPSJNMjcwNS4xNywtMTlDMjcwNS4xNywtMTkgMjc0MC4zOSwtMTkgMjc0MC4zOSwtMTkiLz4KPHBvbHlnb24gZmlsbD0iIzY0NzQ4YiIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgcG9pbnRzPSIyNzQwLjM5LC0yMS42MyAyNzQ3Ljg5LC0xOSAyNzQwLjM5LC0xNi4zOCAyNzQwLjM5LC0yMS42MyIvPgo8L2c+CjwvZz4KPC9zdmc+Cg==" style="max-width: 100%; height: auto;" />
</p>

<details>
<summary>Mermaid source for diagram 3</summary>

```text
flowchart LR
    U["Remote state or connectivity update"] --> C["Transaction commits"]
    C --> E1["RemoteStateChangedEvent<br/>or connectivity event"]
    E1 --> RF["Relevant-property filter"]
    RF --> Q1["Per-remote coalescing queue"]
    Q1 --> RE["Remote health evaluation"]
    RE --> RP["Persist current Remote health"]
    RP --> E2["RemoteHealthChangedEvent"]
    E2 --> GM["Resolve RemoteGroupToRemote memberships"]
    GM --> Q2["Per-group coalescing queue"]
    Q2 --> GE["FleetHealthPolicy evaluation"]
    GE --> GP["Persist RemoteGroup health"]
    GP --> E3["RemoteGroupHealthChangedEvent"]
    E3 --> N["History and notification consumers"]
```

</details>

### 8.1 State ingestion event

After a state update is validated and committed, publish:

```java
public record RemoteStateChangedEvent(
        UUID remoteId,
        UUID stateSchemaId,
        long stateVersion,
        Set<UUID> changedStatePropertyIds,
        OffsetDateTime occurredAt) {
}
```

Use stable property IDs, not property names.

Publish it after commit:

```java
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
public void onRemoteStateChanged(RemoteStateChangedEvent event) {
    healthInvalidationService.invalidateRemote(event);
}
```

The event should contain only change metadata. Consumers can load the latest state if required.

### 8.2 Relevance filtering

Before scheduling health evaluation:

1. Find the remote's effective health profile.
2. Find the canonical mappings used by that profile.
3. Compare their `StatePropertyDefinition` IDs with `changedStatePropertyIds`.
4. Skip evaluation when no health-relevant property changed.

Connectivity, last-seen, certificate, and synchronization events use corresponding built-in event types:

```java
RemoteConnectivityChangedEvent
RemoteLastSeenChangedEvent
RemoteCertificateChangedEvent
RemoteSyncBacklogChangedEvent
```

### 8.3 Remote evaluation request

Do not evaluate synchronously for every incoming state message.

Publish or enqueue an internal request:

```java
public record RemoteHealthEvaluationRequestedEvent(
        UUID remoteId,
        long minimumInputVersion,
        Set<String> reasons) {
}
```

A coalescing service keeps one pending request per remote.

### 8.4 Remote health transition event

After evaluation, persist the current projection. Publish a transition event only when a meaningful field changed:

```java
public record RemoteHealthChangedEvent(
        UUID remoteId,
        String previousSeverity,
        Integer previousSeverityValue,
        String currentSeverity,
        Integer currentSeverityValue,
        UUID matchedRuleId,
        boolean interventionRequired,
        OffsetDateTime changedAt,
        long healthVersion) {
}
```

Do not publish this event when the evaluated result is identical.

### 8.5 Group invalidation

A listener finds active group memberships:

```java
@EventListener
public void onRemoteHealthChanged(RemoteHealthChangedEvent event) {
    remoteGroupHealthInvalidationService.markGroupsDirty(event.remoteId());
}
```

The membership repository uses `RemoteGroupToRemote_.remote`, `activeFrom`, `activeUntil`, and soft-delete metadata.

For each affected group, enqueue:

```java
public record RemoteGroupHealthEvaluationRequestedEvent(
        UUID remoteGroupId,
        Set<UUID> changedRemoteIds,
        Set<String> reasons) {
}
```

Multiple member changes are merged into one pending group request.

### 8.6 Group transition event

After group evaluation:

```java
public record RemoteGroupHealthChangedEvent(
        UUID remoteGroupId,
        String previousSeverity,
        Integer previousSeverityValue,
        String currentSeverity,
        Integer currentSeverityValue,
        UUID matchedFleetRuleId,
        int populationCount,
        int unknownCount,
        int interventionCount,
        OffsetDateTime changedAt,
        long healthVersion) {
}
```

This is the main event for dashboards, alerts, notification adapters, and higher-level parent groups.

---

## 9. Preventing floods

A robust implementation needs several independent controls.

### Flood-control pipeline

<p align="center">
  <img alt="Architecture flow diagram 4" src="data:image/svg+xml;base64,PCEtLSBHZW5lcmF0ZWQgYnkgZ3JhcGh2aXogdmVyc2lvbiAyLjQyLjQgKDApCiAtLT4KPCEtLSBUaXRsZTogRyBQYWdlczogMSAtLT4KPHN2ZyB3aWR0aD0iNjQxcHQiIGhlaWdodD0iMTA2OXB0Igogdmlld0JveD0iMC4wMCAwLjAwIDY0MC41MCAxMDY5LjAwIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIj4KPGcgaWQ9ImdyYXBoMCIgY2xhc3M9ImdyYXBoIiB0cmFuc2Zvcm09InNjYWxlKDEgMSkgcm90YXRlKDApIHRyYW5zbGF0ZSgxOCAxMDUxKSI+Cjx0aXRsZT5HPC90aXRsZT4KPCEtLSBJIC0tPgo8ZyBpZD0ibm9kZTEiIGNsYXNzPSJub2RlIj4KPHRpdGxlPkk8L3RpdGxlPgo8cGF0aCBmaWxsPSIjZjhmYWZjIiBzdHJva2U9IiM0YTU1NjgiIHN0cm9rZS13aWR0aD0iMS4yIiBkPSJNMjI0LC0xMDMzQzIyNCwtMTAzMyA3MiwtMTAzMyA3MiwtMTAzMyA2NiwtMTAzMyA2MCwtMTAyNyA2MCwtMTAyMSA2MCwtMTAyMSA2MCwtMTAwOSA2MCwtMTAwOSA2MCwtMTAwMyA2NiwtOTk3IDcyLC05OTcgNzIsLTk5NyAyMjQsLTk5NyAyMjQsLTk5NyAyMzAsLTk5NyAyMzYsLTEwMDMgMjM2LC0xMDA5IDIzNiwtMTAwOSAyMzYsLTEwMjEgMjM2LC0xMDIxIDIzNiwtMTAyNyAyMzAsLTEwMzMgMjI0LC0xMDMzIi8+Cjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIHg9IjE0OCIgeT0iLTEwMTIuMiIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjExLjAwIiBmaWxsPSIjMWYyOTM3Ij5IaWdoJiM0NTtmcmVxdWVuY3kgc3RhdGUgbWVzc2FnZXM8L3RleHQ+CjwvZz4KPCEtLSBEIC0tPgo8ZyBpZD0ibm9kZTIiIGNsYXNzPSJub2RlIj4KPHRpdGxlPkQ8L3RpdGxlPgo8cG9seWdvbiBmaWxsPSIjZmZmYWYwIiBzdHJva2U9IiM5YTZiMWYiIHN0cm9rZS13aWR0aD0iMS4yIiBwb2ludHM9IjE0OCwtOTUyIDMzLC05MTQgMTQ4LC04NzYgMjYzLC05MTQgMTQ4LC05NTIiLz4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iMTQ4IiB5PSItOTE3LjIiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxMS4wMCIgZmlsbD0iIzFmMjkzNyI+SGVhbHRoJiM0NTtyZWxldmFudDwvdGV4dD4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iMTQ4IiB5PSItOTA1LjIiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxMS4wMCIgZmlsbD0iIzFmMjkzNyI+cHJvcGVydHkgY2hhbmdlZD88L3RleHQ+CjwvZz4KPCEtLSBJJiM0NTsmZ3Q7RCAtLT4KPGcgaWQ9ImVkZ2UxIiBjbGFzcz0iZWRnZSI+Cjx0aXRsZT5JJiM0NTsmZ3Q7RDwvdGl0bGU+CjxwYXRoIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIGQ9Ik0xNDgsLTk5Ni45NEMxNDgsLTk5Ni45NCAxNDgsLTk1OS45IDE0OCwtOTU5LjkiLz4KPHBvbHlnb24gZmlsbD0iIzY0NzQ4YiIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgcG9pbnRzPSIxNTAuNjMsLTk1OS45IDE0OCwtOTUyLjQgMTQ1LjM4LC05NTkuOSAxNTAuNjMsLTk1OS45Ii8+CjwvZz4KPCEtLSBYIC0tPgo8ZyBpZD0ibm9kZTMiIGNsYXNzPSJub2RlIj4KPHRpdGxlPlg8L3RpdGxlPgo8cGF0aCBmaWxsPSIjZjhmYWZjIiBzdHJva2U9IiM0YTU1NjgiIHN0cm9rZS13aWR0aD0iMS4yIiBkPSJNMTUyLC04MjFDMTUyLC04MjEgMTIsLTgyMSAxMiwtODIxIDYsLTgyMSAwLC04MTUgMCwtODA5IDAsLTgwOSAwLC03OTcgMCwtNzk3IDAsLTc5MSA2LC03ODUgMTIsLTc4NSAxMiwtNzg1IDE1MiwtNzg1IDE1MiwtNzg1IDE1OCwtNzg1IDE2NCwtNzkxIDE2NCwtNzk3IDE2NCwtNzk3IDE2NCwtODA5IDE2NCwtODA5IDE2NCwtODE1IDE1OCwtODIxIDE1MiwtODIxIi8+Cjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIHg9IjgyIiB5PSItODAwLjIiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxMS4wMCIgZmlsbD0iIzFmMjkzNyI+RGlzY2FyZCBmb3IgaGVhbHRoIHByb2Nlc3Npbmc8L3RleHQ+CjwvZz4KPCEtLSBEJiM0NTsmZ3Q7WCAtLT4KPGcgaWQ9ImVkZ2UyIiBjbGFzcz0iZWRnZSI+Cjx0aXRsZT5EJiM0NTsmZ3Q7WDwvdGl0bGU+CjxwYXRoIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIGQ9Ik05OC41LC04OTIuMzNDOTguNSwtODkyLjMzIDk4LjUsLTgyOC44IDk4LjUsLTgyOC44Ii8+Cjxwb2x5Z29uIGZpbGw9IiM2NDc0OGIiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIHBvaW50cz0iMTAxLjEzLC04MjguOCA5OC41LC04MjEuMyA5NS44OCwtODI4LjggMTAxLjEzLC04MjguOCIvPgo8dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiB4PSI4OC41IiB5PSItODQ2IiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTAuMDAiIGZpbGw9IiMzMzQxNTUiPk5vPC90ZXh0Pgo8L2c+CjwhLS0gViAtLT4KPGcgaWQ9Im5vZGU0IiBjbGFzcz0ibm9kZSI+Cjx0aXRsZT5WPC90aXRsZT4KPHBhdGggZmlsbD0iI2Y4ZmFmYyIgc3Ryb2tlPSIjNGE1NTY4IiBzdHJva2Utd2lkdGg9IjEuMiIgZD0iTTM4NiwtODIxQzM4NiwtODIxIDIwOCwtODIxIDIwOCwtODIxIDIwMiwtODIxIDE5NiwtODE1IDE5NiwtODA5IDE5NiwtODA5IDE5NiwtNzk3IDE5NiwtNzk3IDE5NiwtNzkxIDIwMiwtNzg1IDIwOCwtNzg1IDIwOCwtNzg1IDM4NiwtNzg1IDM4NiwtNzg1IDM5MiwtNzg1IDM5OCwtNzkxIDM5OCwtNzk3IDM5OCwtNzk3IDM5OCwtODA5IDM5OCwtODA5IDM5OCwtODE1IDM5MiwtODIxIDM4NiwtODIxIi8+Cjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIHg9IjI5NyIgeT0iLTgwMC4yIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTEuMDAiIGZpbGw9IiMxZjI5MzciPktlZXAgbGF0ZXN0IHN0YXRlIHZlcnNpb24gcGVyIFJlbW90ZTwvdGV4dD4KPC9nPgo8IS0tIEQmIzQ1OyZndDtWIC0tPgo8ZyBpZD0iZWRnZTMiIGNsYXNzPSJlZGdlIj4KPHRpdGxlPkQmIzQ1OyZndDtWPC90aXRsZT4KPHBhdGggZmlsbD0ibm9uZSIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgZD0iTTIyOS41LC05MDIuNTRDMjI5LjUsLTkwMi41NCAyMjkuNSwtODI4LjY2IDIyOS41LC04MjguNjYiLz4KPHBvbHlnb24gZmlsbD0iIzY0NzQ4YiIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgcG9pbnRzPSIyMzIuMTMsLTgyOC42NiAyMjkuNSwtODIxLjE2IDIyNi44OCwtODI4LjY2IDIzMi4xMywtODI4LjY2Ii8+Cjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIHg9IjIyMi41IiB5PSItODQ2IiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTAuMDAiIGZpbGw9IiMzMzQxNTUiPlllczwvdGV4dD4KPC9nPgo8IS0tIFJEIC0tPgo8ZyBpZD0ibm9kZTUiIGNsYXNzPSJub2RlIj4KPHRpdGxlPlJEPC90aXRsZT4KPHBhdGggZmlsbD0iI2Y4ZmFmYyIgc3Ryb2tlPSIjNGE1NTY4IiBzdHJva2Utd2lkdGg9IjEuMiIgZD0iTTM2MS41LC03NDBDMzYxLjUsLTc0MCAyMzIuNSwtNzQwIDIzMi41LC03NDAgMjI2LjUsLTc0MCAyMjAuNSwtNzM0IDIyMC41LC03MjggMjIwLjUsLTcyOCAyMjAuNSwtNzE2IDIyMC41LC03MTYgMjIwLjUsLTcxMCAyMjYuNSwtNzA0IDIzMi41LC03MDQgMjMyLjUsLTcwNCAzNjEuNSwtNzA0IDM2MS41LC03MDQgMzY3LjUsLTcwNCAzNzMuNSwtNzEwIDM3My41LC03MTYgMzczLjUsLTcxNiAzNzMuNSwtNzI4IDM3My41LC03MjggMzczLjUsLTczNCAzNjcuNSwtNzQwIDM2MS41LC03NDAiLz4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iMjk3IiB5PSItNzE5LjIiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxMS4wMCIgZmlsbD0iIzFmMjkzNyI+UmVtb3RlIGRlYm91bmNlIHdpbmRvdzwvdGV4dD4KPC9nPgo8IS0tIFYmIzQ1OyZndDtSRCAtLT4KPGcgaWQ9ImVkZ2U0IiBjbGFzcz0iZWRnZSI+Cjx0aXRsZT5WJiM0NTsmZ3Q7UkQ8L3RpdGxlPgo8cGF0aCBmaWxsPSJub25lIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBkPSJNMjk3LC03ODQuNjJDMjk3LC03ODQuNjIgMjk3LC03NDcuNTMgMjk3LC03NDcuNTMiLz4KPHBvbHlnb24gZmlsbD0iIzY0NzQ4YiIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgcG9pbnRzPSIyOTkuNjMsLTc0Ny41MyAyOTcsLTc0MC4wMyAyOTQuMzgsLTc0Ny41MyAyOTkuNjMsLTc0Ny41MyIvPgo8L2c+CjwhLS0gUkUgLS0+CjxnIGlkPSJub2RlNiIgY2xhc3M9Im5vZGUiPgo8dGl0bGU+UkU8L3RpdGxlPgo8cGF0aCBmaWxsPSIjZjhmYWZjIiBzdHJva2U9IiM0YTU1NjgiIHN0cm9rZS13aWR0aD0iMS4yIiBkPSJNMzczLC02NTlDMzczLC02NTkgMjIxLC02NTkgMjIxLC02NTkgMjE1LC02NTkgMjA5LC02NTMgMjA5LC02NDcgMjA5LC02NDcgMjA5LC02MzUgMjA5LC02MzUgMjA5LC02MjkgMjE1LC02MjMgMjIxLC02MjMgMjIxLC02MjMgMzczLC02MjMgMzczLC02MjMgMzc5LC02MjMgMzg1LC02MjkgMzg1LC02MzUgMzg1LC02MzUgMzg1LC02NDcgMzg1LC02NDcgMzg1LC02NTMgMzc5LC02NTkgMzczLC02NTkiLz4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iMjk3IiB5PSItNjM4LjIiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxMS4wMCIgZmlsbD0iIzFmMjkzNyI+RXZhbHVhdGUgb25jZSB1c2luZyBsYXRlc3Qgc3RhdGU8L3RleHQ+CjwvZz4KPCEtLSBSRCYjNDU7Jmd0O1JFIC0tPgo8ZyBpZD0iZWRnZTUiIGNsYXNzPSJlZGdlIj4KPHRpdGxlPlJEJiM0NTsmZ3Q7UkU8L3RpdGxlPgo8cGF0aCBmaWxsPSJub25lIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBkPSJNMjk3LC03MDMuNjJDMjk3LC03MDMuNjIgMjk3LC02NjYuNTMgMjk3LC02NjYuNTMiLz4KPHBvbHlnb24gZmlsbD0iIzY0NzQ4YiIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgcG9pbnRzPSIyOTkuNjMsLTY2Ni41MyAyOTcsLTY1OS4wMyAyOTQuMzgsLTY2Ni41MyAyOTkuNjMsLTY2Ni41MyIvPgo8L2c+CjwhLS0gTyAtLT4KPGcgaWQ9Im5vZGU3IiBjbGFzcz0ibm9kZSI+Cjx0aXRsZT5PPC90aXRsZT4KPHBvbHlnb24gZmlsbD0iI2ZmZmFmMCIgc3Ryb2tlPSIjOWE2YjFmIiBzdHJva2Utd2lkdGg9IjEuMiIgcG9pbnRzPSIyOTcsLTU3OCAxNTgsLTU1MiAyOTcsLTUyNiA0MzYsLTU1MiAyOTcsLTU3OCIvPgo8dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiB4PSIyOTciIHk9Ii01NDkuMiIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjExLjAwIiBmaWxsPSIjMWYyOTM3Ij5IZWFsdGggb3V0cHV0IGNoYW5nZWQ/PC90ZXh0Pgo8L2c+CjwhLS0gUkUmIzQ1OyZndDtPIC0tPgo8ZyBpZD0iZWRnZTYiIGNsYXNzPSJlZGdlIj4KPHRpdGxlPlJFJiM0NTsmZ3Q7TzwvdGl0bGU+CjxwYXRoIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIGQ9Ik0yOTcsLTYyMi45OUMyOTcsLTYyMi45OSAyOTcsLTU4NS41OCAyOTcsLTU4NS41OCIvPgo8cG9seWdvbiBmaWxsPSIjNjQ3NDhiIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBwb2ludHM9IjI5OS42MywtNTg1LjU4IDI5NywtNTc4LjA4IDI5NC4zOCwtNTg1LjU4IDI5OS42MywtNTg1LjU4Ii8+CjwvZz4KPCEtLSBTIC0tPgo8ZyBpZD0ibm9kZTgiIGNsYXNzPSJub2RlIj4KPHRpdGxlPlM8L3RpdGxlPgo8cGF0aCBmaWxsPSIjZjhmYWZjIiBzdHJva2U9IiM0YTU1NjgiIHN0cm9rZS13aWR0aD0iMS4yIiBkPSJNMzExLjUsLTQ3MUMzMTEuNSwtNDcxIDE0OC41LC00NzEgMTQ4LjUsLTQ3MSAxNDIuNSwtNDcxIDEzNi41LC00NjUgMTM2LjUsLTQ1OSAxMzYuNSwtNDU5IDEzNi41LC00NDUgMTM2LjUsLTQ0NSAxMzYuNSwtNDM5IDE0Mi41LC00MzMgMTQ4LjUsLTQzMyAxNDguNSwtNDMzIDMxMS41LC00MzMgMzExLjUsLTQzMyAzMTcuNSwtNDMzIDMyMy41LC00MzkgMzIzLjUsLTQ0NSAzMjMuNSwtNDQ1IDMyMy41LC00NTkgMzIzLjUsLTQ1OSAzMjMuNSwtNDY1IDMxNy41LC00NzEgMzExLjUsLTQ3MSIvPgo8dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiB4PSIyMzAiIHk9Ii00NTUuMiIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjExLjAwIiBmaWxsPSIjMWYyOTM3Ij5VcGRhdGUgZXZhbHVhdGlvbiB0aW1lc3RhbXAgb25seTwvdGV4dD4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iMjMwIiB5PSItNDQzLjIiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxMS4wMCIgZmlsbD0iIzFmMjkzNyI+bm8gdHJhbnNpdGlvbiBldmVudDwvdGV4dD4KPC9nPgo8IS0tIE8mIzQ1OyZndDtTIC0tPgo8ZyBpZD0iZWRnZTciIGNsYXNzPSJlZGdlIj4KPHRpdGxlPk8mIzQ1OyZndDtTPC90aXRsZT4KPHBhdGggZmlsbD0ibm9uZSIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgZD0iTTI0MC43NSwtNTM2LjM4QzI0MC43NSwtNTM2LjM4IDI0MC43NSwtNDc4Ljc4IDI0MC43NSwtNDc4Ljc4Ii8+Cjxwb2x5Z29uIGZpbGw9IiM2NDc0OGIiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIHBvaW50cz0iMjQzLjM4LC00NzguNzggMjQwLjc1LC00NzEuMjggMjM4LjEzLC00NzguNzggMjQzLjM4LC00NzguNzgiLz4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iMjM2LjUiIHk9Ii00OTYiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxMC4wMCIgZmlsbD0iIzMzNDE1NSI+Tm88L3RleHQ+CjwvZz4KPCEtLSBUIC0tPgo8ZyBpZD0ibm9kZTkiIGNsYXNzPSJub2RlIj4KPHRpdGxlPlQ8L3RpdGxlPgo8cGF0aCBmaWxsPSIjZjhmYWZjIiBzdHJva2U9IiM0YTU1NjgiIHN0cm9rZS13aWR0aD0iMS4yIiBkPSJNNTM0LjUsLTQ3MEM1MzQuNSwtNDcwIDM2Ny41LC00NzAgMzY3LjUsLTQ3MCAzNjEuNSwtNDcwIDM1NS41LC00NjQgMzU1LjUsLTQ1OCAzNTUuNSwtNDU4IDM1NS41LC00NDYgMzU1LjUsLTQ0NiAzNTUuNSwtNDQwIDM2MS41LC00MzQgMzY3LjUsLTQzNCAzNjcuNSwtNDM0IDUzNC41LC00MzQgNTM0LjUsLTQzNCA1NDAuNSwtNDM0IDU0Ni41LC00NDAgNTQ2LjUsLTQ0NiA1NDYuNSwtNDQ2IDU0Ni41LC00NTggNTQ2LjUsLTQ1OCA1NDYuNSwtNDY0IDU0MC41LC00NzAgNTM0LjUsLTQ3MCIvPgo8dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiB4PSI0NTEiIHk9Ii00NDkuMiIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjExLjAwIiBmaWxsPSIjMWYyOTM3Ij5QZXJzaXN0IHRyYW5zaXRpb24gYW5kIHB1Ymxpc2ggZXZlbnQ8L3RleHQ+CjwvZz4KPCEtLSBPJiM0NTsmZ3Q7VCAtLT4KPGcgaWQ9ImVkZ2U4IiBjbGFzcz0iZWRnZSI+Cjx0aXRsZT5PJiM0NTsmZ3Q7VDwvdGl0bGU+CjxwYXRoIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIGQ9Ik0zOTUuNzUsLTU0NC4xNUMzOTUuNzUsLTU0NC4xNSAzOTUuNzUsLTQ3Ny42NCAzOTUuNzUsLTQ3Ny42NCIvPgo8cG9seWdvbiBmaWxsPSIjNjQ3NDhiIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBwb2ludHM9IjM5OC4zOCwtNDc3LjY0IDM5NS43NSwtNDcwLjE0IDM5My4xMywtNDc3LjY0IDM5OC4zOCwtNDc3LjY0Ii8+Cjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIHg9IjM3My41IiB5PSItNDk2IiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTAuMDAiIGZpbGw9IiMzMzQxNTUiPlllczwvdGV4dD4KPC9nPgo8IS0tIEdRIC0tPgo8ZyBpZD0ibm9kZTEwIiBjbGFzcz0ibm9kZSI+Cjx0aXRsZT5HUTwvdGl0bGU+CjxwYXRoIGZpbGw9IiNmOGZhZmMiIHN0cm9rZT0iIzRhNTU2OCIgc3Ryb2tlLXdpZHRoPSIxLjIiIGQ9Ik01MzUsLTM4OEM1MzUsLTM4OCAzNjcsLTM4OCAzNjcsLTM4OCAzNjEsLTM4OCAzNTUsLTM4MiAzNTUsLTM3NiAzNTUsLTM3NiAzNTUsLTM2NCAzNTUsLTM2NCAzNTUsLTM1OCAzNjEsLTM1MiAzNjcsLTM1MiAzNjcsLTM1MiA1MzUsLTM1MiA1MzUsLTM1MiA1NDEsLTM1MiA1NDcsLTM1OCA1NDcsLTM2NCA1NDcsLTM2NCA1NDcsLTM3NiA1NDcsLTM3NiA1NDcsLTM4MiA1NDEsLTM4OCA1MzUsLTM4OCIvPgo8dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiB4PSI0NTEiIHk9Ii0zNjcuMiIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjExLjAwIiBmaWxsPSIjMWYyOTM3Ij5NZXJnZSBhZmZlY3RlZCBncm91cHMgYnkgZ3JvdXAgSUQ8L3RleHQ+CjwvZz4KPCEtLSBUJiM0NTsmZ3Q7R1EgLS0+CjxnIGlkPSJlZGdlOSIgY2xhc3M9ImVkZ2UiPgo8dGl0bGU+VCYjNDU7Jmd0O0dRPC90aXRsZT4KPHBhdGggZmlsbD0ibm9uZSIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgZD0iTTQ1MSwtNDMzLjhDNDUxLC00MzMuOCA0NTEsLTM5NS42NSA0NTEsLTM5NS42NSIvPgo8cG9seWdvbiBmaWxsPSIjNjQ3NDhiIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBwb2ludHM9IjQ1My42MywtMzk1LjY1IDQ1MSwtMzg4LjE1IDQ0OC4zOCwtMzk1LjY1IDQ1My42MywtMzk1LjY1Ii8+CjwvZz4KPCEtLSBHRCAtLT4KPGcgaWQ9Im5vZGUxMSIgY2xhc3M9Im5vZGUiPgo8dGl0bGU+R0Q8L3RpdGxlPgo8cGF0aCBmaWxsPSIjZjhmYWZjIiBzdHJva2U9IiM0YTU1NjgiIHN0cm9rZS13aWR0aD0iMS4yIiBkPSJNNTExLjUsLTMwN0M1MTEuNSwtMzA3IDM5MC41LC0zMDcgMzkwLjUsLTMwNyAzODQuNSwtMzA3IDM3OC41LC0zMDEgMzc4LjUsLTI5NSAzNzguNSwtMjk1IDM3OC41LC0yODMgMzc4LjUsLTI4MyAzNzguNSwtMjc3IDM4NC41LC0yNzEgMzkwLjUsLTI3MSAzOTAuNSwtMjcxIDUxMS41LC0yNzEgNTExLjUsLTI3MSA1MTcuNSwtMjcxIDUyMy41LC0yNzcgNTIzLjUsLTI4MyA1MjMuNSwtMjgzIDUyMy41LC0yOTUgNTIzLjUsLTI5NSA1MjMuNSwtMzAxIDUxNy41LC0zMDcgNTExLjUsLTMwNyIvPgo8dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiB4PSI0NTEiIHk9Ii0yODYuMiIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjExLjAwIiBmaWxsPSIjMWYyOTM3Ij5Hcm91cCBkZWJvdW5jZSB3aW5kb3c8L3RleHQ+CjwvZz4KPCEtLSBHUSYjNDU7Jmd0O0dEIC0tPgo8ZyBpZD0iZWRnZTEwIiBjbGFzcz0iZWRnZSI+Cjx0aXRsZT5HUSYjNDU7Jmd0O0dEPC90aXRsZT4KPHBhdGggZmlsbD0ibm9uZSIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgZD0iTTQ1MSwtMzUxLjYyQzQ1MSwtMzUxLjYyIDQ1MSwtMzE0LjUzIDQ1MSwtMzE0LjUzIi8+Cjxwb2x5Z29uIGZpbGw9IiM2NDc0OGIiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIHBvaW50cz0iNDUzLjYzLC0zMTQuNTMgNDUxLC0zMDcuMDMgNDQ4LjM4LC0zMTQuNTMgNDUzLjYzLC0zMTQuNTMiLz4KPC9nPgo8IS0tIElBIC0tPgo8ZyBpZD0ibm9kZTEyIiBjbGFzcz0ibm9kZSI+Cjx0aXRsZT5JQTwvdGl0bGU+CjxwYXRoIGZpbGw9IiNmOGZhZmMiIHN0cm9rZT0iIzRhNTU2OCIgc3Ryb2tlLXdpZHRoPSIxLjIiIGQ9Ik01MzguNSwtMjI2QzUzOC41LC0yMjYgMzYzLjUsLTIyNiAzNjMuNSwtMjI2IDM1Ny41LC0yMjYgMzUxLjUsLTIyMCAzNTEuNSwtMjE0IDM1MS41LC0yMTQgMzUxLjUsLTIwMiAzNTEuNSwtMjAyIDM1MS41LC0xOTYgMzU3LjUsLTE5MCAzNjMuNSwtMTkwIDM2My41LC0xOTAgNTM4LjUsLTE5MCA1MzguNSwtMTkwIDU0NC41LC0xOTAgNTUwLjUsLTE5NiA1NTAuNSwtMjAyIDU1MC41LC0yMDIgNTUwLjUsLTIxNCA1NTAuNSwtMjE0IDU1MC41LC0yMjAgNTQ0LjUsLTIyNiA1MzguNSwtMjI2Ii8+Cjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIHg9IjQ1MSIgeT0iLTIwNS4yIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTEuMDAiIGZpbGw9IiMxZjI5MzciPkFwcGx5IG1lbWJlciBkZWx0YXMgdG8gYWNjdW11bGF0b3I8L3RleHQ+CjwvZz4KPCEtLSBHRCYjNDU7Jmd0O0lBIC0tPgo8ZyBpZD0iZWRnZTExIiBjbGFzcz0iZWRnZSI+Cjx0aXRsZT5HRCYjNDU7Jmd0O0lBPC90aXRsZT4KPHBhdGggZmlsbD0ibm9uZSIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgZD0iTTQ1MSwtMjcwLjYyQzQ1MSwtMjcwLjYyIDQ1MSwtMjMzLjUzIDQ1MSwtMjMzLjUzIi8+Cjxwb2x5Z29uIGZpbGw9IiM2NDc0OGIiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIHBvaW50cz0iNDUzLjYzLC0yMzMuNTMgNDUxLC0yMjYuMDMgNDQ4LjM4LC0yMzMuNTMgNDUzLjYzLC0yMzMuNTMiLz4KPC9nPgo8IS0tIEdPIC0tPgo8ZyBpZD0ibm9kZTEzIiBjbGFzcz0ibm9kZSI+Cjx0aXRsZT5HTzwvdGl0bGU+Cjxwb2x5Z29uIGZpbGw9IiNmZmZhZjAiIHN0cm9rZT0iIzlhNmIxZiIgc3Ryb2tlLXdpZHRoPSIxLjIiIHBvaW50cz0iNDUxLC0xNDUgMzEzLC0xMTkgNDUxLC05MyA1ODksLTExOSA0NTEsLTE0NSIvPgo8dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiB4PSI0NTEiIHk9Ii0xMTYuMiIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjExLjAwIiBmaWxsPSIjMWYyOTM3Ij5Hcm91cCBvdXRwdXQgY2hhbmdlZD88L3RleHQ+CjwvZz4KPCEtLSBJQSYjNDU7Jmd0O0dPIC0tPgo8ZyBpZD0iZWRnZTEyIiBjbGFzcz0iZWRnZSI+Cjx0aXRsZT5JQSYjNDU7Jmd0O0dPPC90aXRsZT4KPHBhdGggZmlsbD0ibm9uZSIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgZD0iTTQ1MSwtMTg5Ljk5QzQ1MSwtMTg5Ljk5IDQ1MSwtMTUyLjU4IDQ1MSwtMTUyLjU4Ii8+Cjxwb2x5Z29uIGZpbGw9IiM2NDc0OGIiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIHBvaW50cz0iNDUzLjYzLC0xNTIuNTggNDUxLC0xNDUuMDggNDQ4LjM4LC0xNTIuNTggNDUzLjYzLC0xNTIuNTgiLz4KPC9nPgo8IS0tIE4gLS0+CjxnIGlkPSJub2RlMTQiIGNsYXNzPSJub2RlIj4KPHRpdGxlPk48L3RpdGxlPgo8cGF0aCBmaWxsPSIjZjhmYWZjIiBzdHJva2U9IiM0YTU1NjgiIHN0cm9rZS13aWR0aD0iMS4yIiBkPSJNNDI1LjUsLTM3QzQyNS41LC0zNyAzMDQuNSwtMzcgMzA0LjUsLTM3IDI5OC41LC0zNyAyOTIuNSwtMzEgMjkyLjUsLTI1IDI5Mi41LC0yNSAyOTIuNSwtMTMgMjkyLjUsLTEzIDI5Mi41LC03IDI5OC41LC0xIDMwNC41LC0xIDMwNC41LC0xIDQyNS41LC0xIDQyNS41LC0xIDQzMS41LC0xIDQzNy41LC03IDQzNy41LC0xMyA0MzcuNSwtMTMgNDM3LjUsLTI1IDQzNy41LC0yNSA0MzcuNSwtMzEgNDMxLjUsLTM3IDQyNS41LC0zNyIvPgo8dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiB4PSIzNjUiIHk9Ii0xNi4yIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTEuMDAiIGZpbGw9IiMxZjI5MzciPk5vIGdyb3VwIHRyYW5zaXRpb24gZXZlbnQ8L3RleHQ+CjwvZz4KPCEtLSBHTyYjNDU7Jmd0O04gLS0+CjxnIGlkPSJlZGdlMTMiIGNsYXNzPSJlZGdlIj4KPHRpdGxlPkdPJiM0NTsmZ3Q7TjwvdGl0bGU+CjxwYXRoIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIGQ9Ik0zNzUuMjUsLTEwNy4xNEMzNzUuMjUsLTEwNy4xNCAzNzUuMjUsLTQ0Ljc5IDM3NS4yNSwtNDQuNzkiLz4KPHBvbHlnb24gZmlsbD0iIzY0NzQ4YiIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgcG9pbnRzPSIzNzcuODgsLTQ0Ljc5IDM3NS4yNSwtMzcuMjkgMzcyLjYzLC00NC43OSAzNzcuODgsLTQ0Ljc5Ii8+Cjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIHg9IjM3MS41IiB5PSItNjMiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxMC4wMCIgZmlsbD0iIzMzNDE1NSI+Tm88L3RleHQ+CjwvZz4KPCEtLSBQIC0tPgo8ZyBpZD0ibm9kZTE1IiBjbGFzcz0ibm9kZSI+Cjx0aXRsZT5QPC90aXRsZT4KPHBhdGggZmlsbD0iI2Y4ZmFmYyIgc3Ryb2tlPSIjNGE1NTY4IiBzdHJva2Utd2lkdGg9IjEuMiIgZD0iTTU5Mi41LC0zOEM1OTIuNSwtMzggNDgxLjUsLTM4IDQ4MS41LC0zOCA0NzUuNSwtMzggNDY5LjUsLTMyIDQ2OS41LC0yNiA0NjkuNSwtMjYgNDY5LjUsLTEyIDQ2OS41LC0xMiA0NjkuNSwtNiA0NzUuNSwwIDQ4MS41LDAgNDgxLjUsMCA1OTIuNSwwIDU5Mi41LDAgNTk4LjUsMCA2MDQuNSwtNiA2MDQuNSwtMTIgNjA0LjUsLTEyIDYwNC41LC0yNiA2MDQuNSwtMjYgNjA0LjUsLTMyIDU5OC41LC0zOCA1OTIuNSwtMzgiLz4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iNTM3IiB5PSItMjIuMiIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjExLjAwIiBmaWxsPSIjMWYyOTM3Ij5QZXJzaXN0IGdyb3VwIHRyYW5zaXRpb248L3RleHQ+Cjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIHg9IjUzNyIgeT0iLTEwLjIiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxMS4wMCIgZmlsbD0iIzFmMjkzNyI+cHVibGlzaCBvbmUgZXZlbnQ8L3RleHQ+CjwvZz4KPCEtLSBHTyYjNDU7Jmd0O1AgLS0+CjxnIGlkPSJlZGdlMTQiIGNsYXNzPSJlZGdlIj4KPHRpdGxlPkdPJiM0NTsmZ3Q7UDwvdGl0bGU+CjxwYXRoIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIGQ9Ik01MjkuMjUsLTEwNy41M0M1MjkuMjUsLTEwNy41MyA1MjkuMjUsLTQ1LjY5IDUyOS4yNSwtNDUuNjkiLz4KPHBvbHlnb24gZmlsbD0iIzY0NzQ4YiIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgcG9pbnRzPSI1MzEuODgsLTQ1LjY5IDUyOS4yNSwtMzguMTkgNTI2LjYzLC00NS42OSA1MzEuODgsLTQ1LjY5Ii8+Cjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIHg9IjU0NS41IiB5PSItNjMiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxMC4wMCIgZmlsbD0iIzMzNDE1NSI+WWVzPC90ZXh0Pgo8L2c+CjwvZz4KPC9zdmc+Cg==" style="max-width: 100%; height: auto;" />
</p>

<details>
<summary>Mermaid source for diagram 4</summary>

```text
flowchart TB
    I["High-frequency state messages"] --> D{"Health-relevant<br/>property changed?"}
    D -- "No" --> X["Discard for health processing"]
    D -- "Yes" --> V["Keep latest state version per Remote"]
    V --> RD["Remote debounce window"]
    RD --> RE["Evaluate once using latest state"]
    RE --> O{"Health output changed?"}
    O -- "No" --> S["Update evaluation timestamp only<br/>no transition event"]
    O -- "Yes" --> T["Persist transition and publish event"]
    T --> GQ["Merge affected groups by group ID"]
    GQ --> GD["Group debounce window"]
    GD --> IA["Apply member deltas to accumulator"]
    IA --> GO{"Group output changed?"}
    GO -- "No" --> N["No group transition event"]
    GO -- "Yes" --> P["Persist group transition<br/>publish one event"]
```

</details>

### 9.1 Changed-property filtering

Do not evaluate health unless a relevant source property or built-in signal changed.

This removes most telemetry updates from the health pipeline.

### 9.2 Per-remote coalescing

Maintain one pending evaluation per remote:

```text
remoteId → latest requested input version + merged reasons
```

If 100 state messages arrive within 500 ms, evaluate once using the latest committed state.

Recommended default:

```text
Remote debounce window: 250–1000 ms
```

### 9.3 Per-group coalescing

Maintain one pending evaluation per group:

```text
groupId → changed member IDs + latest request time
```

If 200 members change in a short interval, evaluate the group once.

Recommended default:

```text
Group debounce window: 1–5 seconds
```

Large fleet groups may use 10–30 seconds.

### 9.4 Hysteresis and stable duration

A rule should support:

```text
minimumStableMillis
recoveryStableMillis
```

Example:

```text
Offline must remain true for 30 seconds before Warning.
Offline must remain true for 5 minutes before Critical.
Online must remain true for 20 seconds before recovery.
```

This prevents connection flapping from producing repeated transitions.

### 9.5 Input and output hashes

Store:

```text
healthInputVersion
healthInputHash
healthOutputHash
```

Skip evaluation when the relevant input hash did not change.

Skip persistence and events when the output hash did not change.

### 9.6 Incremental aggregates

For large groups, maintain an aggregate projection:

```java
@Entity
public class RemoteGroupHealthAccumulator extends Baseclass {

    @ManyToOne
    private RemoteGroup remoteGroup;

    private Integer totalMembers;
    private Integer onlineMembers;
    private Integer offlineMembers;
    private Integer interventionMembers;

    private Double totalWeight;
    private Double weightedSeveritySum;

    private Long accumulatorVersion;
}
```

When a member changes from Warning to Critical, update the delta:

```text
warningCount - 1
criticalCount + 1
weightedSeveritySum += weight * (100 - 40)
```

Do not rescan all members for every event.

A periodic reconciliation job can verify the accumulator against source data.

### 9.7 Severity bucket counters

Maintain counters by severity threshold or severity value:

```text
severity 0 count
severity 10 count
severity 40 count
severity 60 count
severity 100 count
```

This makes metrics such as "Major or higher percentage" inexpensive.

### 9.8 Partitioned executors

Partition work by entity ID:

```text
hash(remoteId) % N
hash(groupId) % N
```

Events for one remote or one group remain ordered, while unrelated entities process in parallel.

### 9.9 Backpressure

Use bounded queues. When overloaded:

- Replace older pending evaluations for the same ID with the newest version.
- Never queue thousands of duplicate evaluations.
- Mark groups dirty and reconcile later rather than losing the latest state.

### 9.10 Notification deduplication

Notifications should use:

```text
transition
severity escalation
intervention flag change
reminder interval
```

Do not notify on every evaluation.

---

## 10. Historical storage

Current projections and history should be separate.

### Current-state and history persistence flow

<p align="center">
  <img alt="Architecture flow diagram 5" src="data:image/svg+xml;base64,PCEtLSBHZW5lcmF0ZWQgYnkgZ3JhcGh2aXogdmVyc2lvbiAyLjQyLjQgKDApCiAtLT4KPCEtLSBUaXRsZTogRyBQYWdlczogMSAtLT4KPHN2ZyB3aWR0aD0iMTc2OXB0IiBoZWlnaHQ9IjIwOXB0Igogdmlld0JveD0iMC4wMCAwLjAwIDE3NjkuMDAgMjA5LjAwIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIj4KPGcgaWQ9ImdyYXBoMCIgY2xhc3M9ImdyYXBoIiB0cmFuc2Zvcm09InNjYWxlKDEgMSkgcm90YXRlKDApIHRyYW5zbGF0ZSgxOCAxOTEpIj4KPHRpdGxlPkc8L3RpdGxlPgo8IS0tIEVWIC0tPgo8ZyBpZD0ibm9kZTEiIGNsYXNzPSJub2RlIj4KPHRpdGxlPkVWPC90aXRsZT4KPHBhdGggZmlsbD0iI2Y4ZmFmYyIgc3Ryb2tlPSIjNGE1NTY4IiBzdHJva2Utd2lkdGg9IjEuMiIgZD0iTTEyNSwtMTM5QzEyNSwtMTM5IDEyLC0xMzkgMTIsLTEzOSA2LC0xMzkgMCwtMTMzIDAsLTEyNyAwLC0xMjcgMCwtMTE1IDAsLTExNSAwLC0xMDkgNiwtMTAzIDEyLC0xMDMgMTIsLTEwMyAxMjUsLTEwMyAxMjUsLTEwMyAxMzEsLTEwMyAxMzcsLTEwOSAxMzcsLTExNSAxMzcsLTExNSAxMzcsLTEyNyAxMzcsLTEyNyAxMzcsLTEzMyAxMzEsLTEzOSAxMjUsLTEzOSIvPgo8dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiB4PSI2OC41IiB5PSItMTE4LjIiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxMS4wMCIgZmlsbD0iIzFmMjkzNyI+SGVhbHRoIGV2YWx1YXRpb24gcmVzdWx0PC90ZXh0Pgo8L2c+CjwhLS0gQ0ggLS0+CjxnIGlkPSJub2RlMiIgY2xhc3M9Im5vZGUiPgo8dGl0bGU+Q0g8L3RpdGxlPgo8cG9seWdvbiBmaWxsPSIjZmZmYWYwIiBzdHJva2U9IiM5YTZiMWYiIHN0cm9rZS13aWR0aD0iMS4yIiBwb2ludHM9IjMwMywtMTQ3IDE4MiwtMTIxIDMwMywtOTUgNDI0LC0xMjEgMzAzLC0xNDciLz4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iMzAzIiB5PSItMTE4LjIiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxMS4wMCIgZmlsbD0iIzFmMjkzNyI+TWVhbmluZ2Z1bCBjaGFuZ2U/PC90ZXh0Pgo8L2c+CjwhLS0gRVYmIzQ1OyZndDtDSCAtLT4KPGcgaWQ9ImVkZ2UxIiBjbGFzcz0iZWRnZSI+Cjx0aXRsZT5FViYjNDU7Jmd0O0NIPC90aXRsZT4KPHBhdGggZmlsbD0ibm9uZSIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgZD0iTTEzNy4yNywtMTIxQzEzNy4yNywtMTIxIDE3NC4yOCwtMTIxIDE3NC4yOCwtMTIxIi8+Cjxwb2x5Z29uIGZpbGw9IiM2NDc0OGIiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIHBvaW50cz0iMTc0LjI4LC0xMjMuNjMgMTgxLjc4LC0xMjEgMTc0LjI4LC0xMTguMzggMTc0LjI4LC0xMjMuNjMiLz4KPC9nPgo8IS0tIENQIC0tPgo8ZyBpZD0ibm9kZTMiIGNsYXNzPSJub2RlIj4KPHRpdGxlPkNQPC90aXRsZT4KPHBhdGggZmlsbD0iI2Y4ZmFmYyIgc3Ryb2tlPSIjNGE1NTY4IiBzdHJva2Utd2lkdGg9IjEuMiIgZD0iTTY3MCwtMTczQzY3MCwtMTczIDQ5NywtMTczIDQ5NywtMTczIDQ5MSwtMTczIDQ4NSwtMTY3IDQ4NSwtMTYxIDQ4NSwtMTYxIDQ4NSwtMTQ5IDQ4NSwtMTQ5IDQ4NSwtMTQzIDQ5MSwtMTM3IDQ5NywtMTM3IDQ5NywtMTM3IDY3MCwtMTM3IDY3MCwtMTM3IDY3NiwtMTM3IDY4MiwtMTQzIDY4MiwtMTQ5IDY4MiwtMTQ5IDY4MiwtMTYxIDY4MiwtMTYxIDY4MiwtMTY3IDY3NiwtMTczIDY3MCwtMTczIi8+Cjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIHg9IjU4My41IiB5PSItMTUyLjIiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxMS4wMCIgZmlsbD0iIzFmMjkzNyI+UmVmcmVzaCBjdXJyZW50IHByb2plY3Rpb24gbWV0YWRhdGE8L3RleHQ+CjwvZz4KPCEtLSBDSCYjNDU7Jmd0O0NQIC0tPgo8ZyBpZD0iZWRnZTIiIGNsYXNzPSJlZGdlIj4KPHRpdGxlPkNIJiM0NTsmZ3Q7Q1A8L3RpdGxlPgo8cGF0aCBmaWxsPSJub25lIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBkPSJNMzI2LjQzLC0xNDJDMzI2LjQzLC0xNDIgNDc3LjMxLC0xNDIgNDc3LjMxLC0xNDIiLz4KPHBvbHlnb24gZmlsbD0iIzY0NzQ4YiIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgcG9pbnRzPSI0NzcuMzEsLTE0NC42MyA0ODQuODEsLTE0MiA0NzcuMzEsLTEzOS4zOCA0NzcuMzEsLTE0NC42MyIvPgo8dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiB4PSI0NTQuNSIgeT0iLTE1OCIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjEwLjAwIiBmaWxsPSIjMzM0MTU1Ij5ObzwvdGV4dD4KPC9nPgo8IS0tIENMT1NFIC0tPgo8ZyBpZD0ibm9kZTQiIGNsYXNzPSJub2RlIj4KPHRpdGxlPkNMT1NFPC90aXRsZT4KPHBhdGggZmlsbD0iI2Y4ZmFmYyIgc3Ryb2tlPSIjNGE1NTY4IiBzdHJva2Utd2lkdGg9IjEuMiIgZD0iTTY1NS41LC0xMDVDNjU1LjUsLTEwNSA1MTEuNSwtMTA1IDUxMS41LC0xMDUgNTA1LjUsLTEwNSA0OTkuNSwtOTkgNDk5LjUsLTkzIDQ5OS41LC05MyA0OTkuNSwtNzkgNDk5LjUsLTc5IDQ5OS41LC03MyA1MDUuNSwtNjcgNTExLjUsLTY3IDUxMS41LC02NyA2NTUuNSwtNjcgNjU1LjUsLTY3IDY2MS41LC02NyA2NjcuNSwtNzMgNjY3LjUsLTc5IDY2Ny41LC03OSA2NjcuNSwtOTMgNjY3LjUsLTkzIDY2Ny41LC05OSA2NjEuNSwtMTA1IDY1NS41LC0xMDUiLz4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iNTgzLjUiIHk9Ii04OS4yIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTEuMDAiIGZpbGw9IiMxZjI5MzciPkNsb3NlIHByZXZpb3VzIGhpc3RvcnkgaW50ZXJ2YWw8L3RleHQ+Cjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIHg9IjU4My41IiB5PSItNzcuMiIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjExLjAwIiBmaWxsPSIjMWYyOTM3Ij5zZXQgdmFsaWRVbnRpbDwvdGV4dD4KPC9nPgo8IS0tIENIJiM0NTsmZ3Q7Q0xPU0UgLS0+CjxnIGlkPSJlZGdlMyIgY2xhc3M9ImVkZ2UiPgo8dGl0bGU+Q0gmIzQ1OyZndDtDTE9TRTwvdGl0bGU+CjxwYXRoIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIGQ9Ik0zMjYuNDMsLTEwMEMzMjYuNDMsLTEwMCA0OTEuOTcsLTEwMCA0OTEuOTcsLTEwMCIvPgo8cG9seWdvbiBmaWxsPSIjNjQ3NDhiIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBwb2ludHM9IjQ5MS45NywtMTAyLjYzIDQ5OS40NywtMTAwIDQ5MS45NywtOTcuMzggNDkxLjk3LC0xMDIuNjMiLz4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iNDU0LjUiIHk9Ii0xMDAiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxMC4wMCIgZmlsbD0iIzMzNDE1NSI+WWVzPC90ZXh0Pgo8L2c+CjwhLS0gTkVXIC0tPgo8ZyBpZD0ibm9kZTUiIGNsYXNzPSJub2RlIj4KPHRpdGxlPk5FVzwvdGl0bGU+CjxwYXRoIGZpbGw9IiNmOGZhZmMiIHN0cm9rZT0iIzRhNTU2OCIgc3Ryb2tlLXdpZHRoPSIxLjIiIGQ9Ik04NjIsLTEwNEM4NjIsLTEwNCA3MzksLTEwNCA3MzksLTEwNCA3MzMsLTEwNCA3MjcsLTk4IDcyNywtOTIgNzI3LC05MiA3MjcsLTgwIDcyNywtODAgNzI3LC03NCA3MzMsLTY4IDczOSwtNjggNzM5LC02OCA4NjIsLTY4IDg2MiwtNjggODY4LC02OCA4NzQsLTc0IDg3NCwtODAgODc0LC04MCA4NzQsLTkyIDg3NCwtOTIgODc0LC05OCA4NjgsLTEwNCA4NjIsLTEwNCIvPgo8dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiB4PSI4MDAuNSIgeT0iLTgzLjIiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxMS4wMCIgZmlsbD0iIzFmMjkzNyI+SW5zZXJ0IG5ldyBoaXN0b3J5IGludGVydmFsPC90ZXh0Pgo8L2c+CjwhLS0gQ0xPU0UmIzQ1OyZndDtORVcgLS0+CjxnIGlkPSJlZGdlNCIgY2xhc3M9ImVkZ2UiPgo8dGl0bGU+Q0xPU0UmIzQ1OyZndDtORVc8L3RpdGxlPgo8cGF0aCBmaWxsPSJub25lIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBkPSJNNjY3LjcxLC04NkM2NjcuNzEsLTg2IDcxOS40MywtODYgNzE5LjQzLC04NiIvPgo8cG9seWdvbiBmaWxsPSIjNjQ3NDhiIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBwb2ludHM9IjcxOS40MywtODguNjMgNzI2LjkzLC04NiA3MTkuNDMsLTgzLjM4IDcxOS40MywtODguNjMiLz4KPC9nPgo8IS0tIENVUiAtLT4KPGcgaWQ9Im5vZGU2IiBjbGFzcz0ibm9kZSI+Cjx0aXRsZT5DVVI8L3RpdGxlPgo8cGF0aCBmaWxsPSIjZjhmYWZjIiBzdHJva2U9IiM0YTU1NjgiIHN0cm9rZS13aWR0aD0iMS4yIiBkPSJNMTA4NSwtMTA0QzEwODUsLTEwNCA5MzEsLTEwNCA5MzEsLTEwNCA5MjUsLTEwNCA5MTksLTk4IDkxOSwtOTIgOTE5LC05MiA5MTksLTgwIDkxOSwtODAgOTE5LC03NCA5MjUsLTY4IDkzMSwtNjggOTMxLC02OCAxMDg1LC02OCAxMDg1LC02OCAxMDkxLC02OCAxMDk3LC03NCAxMDk3LC04MCAxMDk3LC04MCAxMDk3LC05MiAxMDk3LC05MiAxMDk3LC05OCAxMDkxLC0xMDQgMTA4NSwtMTA0Ii8+Cjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIHg9IjEwMDgiIHk9Ii04My4yIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTEuMDAiIGZpbGw9IiMxZjI5MzciPlVwZGF0ZSBjdXJyZW50IGhlYWx0aCBwcm9qZWN0aW9uPC90ZXh0Pgo8L2c+CjwhLS0gTkVXJiM0NTsmZ3Q7Q1VSIC0tPgo8ZyBpZD0iZWRnZTUiIGNsYXNzPSJlZGdlIj4KPHRpdGxlPk5FVyYjNDU7Jmd0O0NVUjwvdGl0bGU+CjxwYXRoIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIGQ9Ik04NzQuMjQsLTg2Qzg3NC4yNCwtODYgOTExLjM2LC04NiA5MTEuMzYsLTg2Ii8+Cjxwb2x5Z29uIGZpbGw9IiM2NDc0OGIiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIHBvaW50cz0iOTExLjM2LC04OC42MyA5MTguODYsLTg2IDkxMS4zNiwtODMuMzggOTExLjM2LC04OC42MyIvPgo8L2c+CjwhLS0gT1VUIC0tPgo8ZyBpZD0ibm9kZTciIGNsYXNzPSJub2RlIj4KPHRpdGxlPk9VVDwvdGl0bGU+CjxwYXRoIGZpbGw9IiNmOGZhZmMiIHN0cm9rZT0iIzRhNTU2OCIgc3Ryb2tlLXdpZHRoPSIxLjIiIGQ9Ik0xMzAyLC0xMDRDMTMwMiwtMTA0IDExNTQsLTEwNCAxMTU0LC0xMDQgMTE0OCwtMTA0IDExNDIsLTk4IDExNDIsLTkyIDExNDIsLTkyIDExNDIsLTgwIDExNDIsLTgwIDExNDIsLTc0IDExNDgsLTY4IDExNTQsLTY4IDExNTQsLTY4IDEzMDIsLTY4IDEzMDIsLTY4IDEzMDgsLTY4IDEzMTQsLTc0IDEzMTQsLTgwIDEzMTQsLTgwIDEzMTQsLTkyIDEzMTQsLTkyIDEzMTQsLTk4IDEzMDgsLTEwNCAxMzAyLC0xMDQiLz4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iMTIyOCIgeT0iLTgzLjIiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxMS4wMCIgZmlsbD0iIzFmMjkzNyI+SW5zZXJ0IHRyYW5zYWN0aW9uYWwgb3V0Ym94IHJvdzwvdGV4dD4KPC9nPgo8IS0tIENVUiYjNDU7Jmd0O09VVCAtLT4KPGcgaWQ9ImVkZ2U2IiBjbGFzcz0iZWRnZSI+Cjx0aXRsZT5DVVImIzQ1OyZndDtPVVQ8L3RpdGxlPgo8cGF0aCBmaWxsPSJub25lIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBkPSJNMTA5Ny4xNywtODZDMTA5Ny4xNywtODYgMTEzNC40MiwtODYgMTEzNC40MiwtODYiLz4KPHBvbHlnb24gZmlsbD0iIzY0NzQ4YiIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgcG9pbnRzPSIxMTM0LjQyLC04OC42MyAxMTQxLjkyLC04NiAxMTM0LjQyLC04My4zOCAxMTM0LjQyLC04OC42MyIvPgo8L2c+CjwhLS0gQ09NTUlUIC0tPgo8ZyBpZD0ibm9kZTgiIGNsYXNzPSJub2RlIj4KPHRpdGxlPkNPTU1JVDwvdGl0bGU+CjxwYXRoIGZpbGw9IiNmOGZhZmMiIHN0cm9rZT0iIzRhNTU2OCIgc3Ryb2tlLXdpZHRoPSIxLjIiIGQ9Ik0xNDU5LC0xMDRDMTQ1OSwtMTA0IDEzNzEsLTEwNCAxMzcxLC0xMDQgMTM2NSwtMTA0IDEzNTksLTk4IDEzNTksLTkyIDEzNTksLTkyIDEzNTksLTgwIDEzNTksLTgwIDEzNTksLTc0IDEzNjUsLTY4IDEzNzEsLTY4IDEzNzEsLTY4IDE0NTksLTY4IDE0NTksLTY4IDE0NjUsLTY4IDE0NzEsLTc0IDE0NzEsLTgwIDE0NzEsLTgwIDE0NzEsLTkyIDE0NzEsLTkyIDE0NzEsLTk4IDE0NjUsLTEwNCAxNDU5LC0xMDQiLz4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iMTQxNSIgeT0iLTgzLjIiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxMS4wMCIgZmlsbD0iIzFmMjkzNyI+Q29tbWl0IGF0b21pY2FsbHk8L3RleHQ+CjwvZz4KPCEtLSBPVVQmIzQ1OyZndDtDT01NSVQgLS0+CjxnIGlkPSJlZGdlNyIgY2xhc3M9ImVkZ2UiPgo8dGl0bGU+T1VUJiM0NTsmZ3Q7Q09NTUlUPC90aXRsZT4KPHBhdGggZmlsbD0ibm9uZSIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgZD0iTTEzMTQuMTEsLTg2QzEzMTQuMTEsLTg2IDEzNTEuNDksLTg2IDEzNTEuNDksLTg2Ii8+Cjxwb2x5Z29uIGZpbGw9IiM2NDc0OGIiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIHBvaW50cz0iMTM1MS40OSwtODguNjMgMTM1OC45OSwtODYgMTM1MS40OSwtODMuMzggMTM1MS40OSwtODguNjMiLz4KPC9nPgo8IS0tIFJFQUQxIC0tPgo8ZyBpZD0ibm9kZTkiIGNsYXNzPSJub2RlIj4KPHRpdGxlPlJFQUQxPC90aXRsZT4KPHBhdGggZmlsbD0iI2Y4ZmFmYyIgc3Ryb2tlPSIjNGE1NTY4IiBzdHJva2Utd2lkdGg9IjEuMiIgZD0iTTE3MjEsLTE3MkMxNzIxLC0xNzIgMTUyOCwtMTcyIDE1MjgsLTE3MiAxNTIyLC0xNzIgMTUxNiwtMTY2IDE1MTYsLTE2MCAxNTE2LC0xNjAgMTUxNiwtMTQ4IDE1MTYsLTE0OCAxNTE2LC0xNDIgMTUyMiwtMTM2IDE1MjgsLTEzNiAxNTI4LC0xMzYgMTcyMSwtMTM2IDE3MjEsLTEzNiAxNzI3LC0xMzYgMTczMywtMTQyIDE3MzMsLTE0OCAxNzMzLC0xNDggMTczMywtMTYwIDE3MzMsLTE2MCAxNzMzLC0xNjYgMTcyNywtMTcyIDE3MjEsLTE3MiIvPgo8dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiB4PSIxNjI0LjUiIHk9Ii0xNTEuMiIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjExLjAwIiBmaWxsPSIjMWYyOTM3Ij5PcGVyYXRpb25hbCBBUElzIHJlYWQgY3VycmVudCBwcm9qZWN0aW9uPC90ZXh0Pgo8L2c+CjwhLS0gQ09NTUlUJiM0NTsmZ3Q7UkVBRDEgLS0+CjxnIGlkPSJlZGdlOCIgY2xhc3M9ImVkZ2UiPgo8dGl0bGU+Q09NTUlUJiM0NTsmZ3Q7UkVBRDE8L3RpdGxlPgo8cGF0aCBmaWxsPSJub25lIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBkPSJNMTQ3MS4yMSwtOTJDMTQ5Ny40NiwtOTIgMTUyMywtOTIgMTUyMywtOTIgMTUyMywtOTIgMTUyMywtMTI4LjIzIDE1MjMsLTEyOC4yMyIvPgo8cG9seWdvbiBmaWxsPSIjNjQ3NDhiIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBwb2ludHM9IjE1MjAuMzgsLTEyOC4yMyAxNTIzLC0xMzUuNzMgMTUyNS42MywtMTI4LjIzIDE1MjAuMzgsLTEyOC4yMyIvPgo8L2c+CjwhLS0gUkVBRDIgLS0+CjxnIGlkPSJub2RlMTAiIGNsYXNzPSJub2RlIj4KPHRpdGxlPlJFQUQyPC90aXRsZT4KPHBhdGggZmlsbD0iI2Y4ZmFmYyIgc3Ryb2tlPSIjNGE1NTY4IiBzdHJva2Utd2lkdGg9IjEuMiIgZD0iTTE3MDgsLTEwNEMxNzA4LC0xMDQgMTU0MSwtMTA0IDE1NDEsLTEwNCAxNTM1LC0xMDQgMTUyOSwtOTggMTUyOSwtOTIgMTUyOSwtOTIgMTUyOSwtODAgMTUyOSwtODAgMTUyOSwtNzQgMTUzNSwtNjggMTU0MSwtNjggMTU0MSwtNjggMTcwOCwtNjggMTcwOCwtNjggMTcxNCwtNjggMTcyMCwtNzQgMTcyMCwtODAgMTcyMCwtODAgMTcyMCwtOTIgMTcyMCwtOTIgMTcyMCwtOTggMTcxNCwtMTA0IDE3MDgsLTEwNCIvPgo8dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiB4PSIxNjI0LjUiIHk9Ii04My4yIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTEuMDAiIGZpbGw9IiMxZjI5MzciPkhpc3RvcmljYWwgQVBJcyByZWFkIGludGVydmFsIGhpc3Rvcnk8L3RleHQ+CjwvZz4KPCEtLSBDT01NSVQmIzQ1OyZndDtSRUFEMiAtLT4KPGcgaWQ9ImVkZ2U5IiBjbGFzcz0iZWRnZSI+Cjx0aXRsZT5DT01NSVQmIzQ1OyZndDtSRUFEMjwvdGl0bGU+CjxwYXRoIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIGQ9Ik0xNDcxLjE0LC04MEMxNDcxLjE0LC04MCAxNTIxLjQ3LC04MCAxNTIxLjQ3LC04MCIvPgo8cG9seWdvbiBmaWxsPSIjNjQ3NDhiIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBwb2ludHM9IjE1MjEuNDcsLTgyLjYzIDE1MjguOTcsLTgwIDE1MjEuNDcsLTc3LjM4IDE1MjEuNDcsLTgyLjYzIi8+CjwvZz4KPCEtLSBQVUIgLS0+CjxnIGlkPSJub2RlMTEiIGNsYXNzPSJub2RlIj4KPHRpdGxlPlBVQjwvdGl0bGU+CjxwYXRoIGZpbGw9IiNmOGZhZmMiIHN0cm9rZT0iIzRhNTU2OCIgc3Ryb2tlLXdpZHRoPSIxLjIiIGQ9Ik0xNzAwLC0zNkMxNzAwLC0zNiAxNTQ5LC0zNiAxNTQ5LC0zNiAxNTQzLC0zNiAxNTM3LC0zMCAxNTM3LC0yNCAxNTM3LC0yNCAxNTM3LC0xMiAxNTM3LC0xMiAxNTM3LC02IDE1NDMsMCAxNTQ5LDAgMTU0OSwwIDE3MDAsMCAxNzAwLDAgMTcwNiwwIDE3MTIsLTYgMTcxMiwtMTIgMTcxMiwtMTIgMTcxMiwtMjQgMTcxMiwtMjQgMTcxMiwtMzAgMTcwNiwtMzYgMTcwMCwtMzYiLz4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iMTYyNC41IiB5PSItMTUuMiIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjExLjAwIiBmaWxsPSIjMWYyOTM3Ij5PdXRib3ggcHVibGlzaGVyIGRlbGl2ZXJzIGV2ZW50PC90ZXh0Pgo8L2c+CjwhLS0gQ09NTUlUJiM0NTsmZ3Q7UFVCIC0tPgo8ZyBpZD0iZWRnZTEwIiBjbGFzcz0iZWRnZSI+Cjx0aXRsZT5DT01NSVQmIzQ1OyZndDtQVUI8L3RpdGxlPgo8cGF0aCBmaWxsPSJub25lIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBkPSJNMTQxNSwtNjcuNzhDMTQxNSwtNDcuNjMgMTQxNSwtMTggMTQxNSwtMTggMTQxNSwtMTggMTUyOS4zNywtMTggMTUyOS4zNywtMTgiLz4KPHBvbHlnb24gZmlsbD0iIzY0NzQ4YiIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgcG9pbnRzPSIxNTI5LjM3LC0yMC42MyAxNTM2Ljg3LC0xOCAxNTI5LjM3LC0xNS4zOCAxNTI5LjM3LC0yMC42MyIvPgo8L2c+CjwvZz4KPC9zdmc+Cg==" style="max-width: 100%; height: auto;" />
</p>

<details>
<summary>Mermaid source for diagram 5</summary>

```text
flowchart LR
    EV["Health evaluation result"] --> CH{"Meaningful change?"}
    CH -- "No" --> CP["Refresh current projection metadata"]
    CH -- "Yes" --> CLOSE["Close previous history interval<br/>set validUntil"]
    CLOSE --> NEW["Insert new history interval"]
    NEW --> CUR["Update current health projection"]
    CUR --> OUT["Insert transactional outbox row"]
    OUT --> COMMIT["Commit atomically"]

    COMMIT --> READ1["Operational APIs read current projection"]
    COMMIT --> READ2["Historical APIs read interval history"]
    COMMIT --> PUB["Outbox publisher delivers event"]
```

</details>

### 10.1 Current remote health

Stored on `Remote` or in a one-to-one `RemoteHealthCurrent` table.

Purpose:

- Fast reads.
- Dashboard grids.
- Fleet aggregation.
- Current-status APIs.

### 10.2 Remote health history

```java
@Entity
public class RemoteHealthHistory extends Baseclass {

    @ManyToOne(targetEntity = Remote.class)
    private Remote remote;

    @ManyToOne(targetEntity = RemoteHealthProfile.class)
    private RemoteHealthProfile profile;

    @ManyToOne(targetEntity = RemoteHealthRule.class)
    private RemoteHealthRule matchedRule;

    private String severityName;
    private Integer severityValue;

    private Boolean humanInterventionRequired;
    private String summary;

    private OffsetDateTime validFrom;
    private OffsetDateTime validUntil;

    private Long sourceStateVersion;
    private String canonicalSignalSnapshotHash;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> relevantSignalSnapshot;
}
```

Although rules should not be stored as free-form JSON, a small immutable signal snapshot is useful for audit evidence.

Store only relevant canonical signals, not the entire device state.

Example:

```json
{
  "CONNECTIVITY": "ONLINE",
  "ACTUATOR_BLOCKED": true,
  "SUPPLY_VOLTAGE_VOLTS": 10.8
}
```

Create a history row when:

- Severity changes.
- Matched rule changes.
- Human-intervention flag changes.
- Mitigation status changes.
- A configured periodic snapshot interval is reached.

Close the previous interval by setting `validUntil`.

### 10.3 RemoteGroup health history

```java
@Entity
public class RemoteGroupHealthHistory extends Baseclass {

    @ManyToOne(targetEntity = RemoteGroup.class)
    private RemoteGroup remoteGroup;

    @ManyToOne(targetEntity = FleetHealthPolicy.class)
    private FleetHealthPolicy policy;

    @ManyToOne(targetEntity = FleetHealthRule.class)
    private FleetHealthRule matchedRule;

    private String severityName;
    private Integer severityValue;

    private Integer populationCount;
    private Integer unknownCount;
    private Integer interventionCount;
    private Integer offlineCount;

    private Double weightedSeverityAverage;

    private OffsetDateTime validFrom;
    private OffsetDateTime validUntil;

    private Long accumulatorVersion;
    private String aggregateHash;
}
```

Optional evidence can be stored in normalized child rows:

```java
@Entity
public class RemoteGroupHealthMetricHistory extends Baseclass {

    @ManyToOne
    private RemoteGroupHealthHistory groupHealthHistory;

    @Enumerated(EnumType.STRING)
    private FleetHealthMetric metric;

    private Double value;
    private Double denominator;

    @ManyToOne
    private RemoteRoleDefinition role;

    @ManyToOne
    private HealthSignalDefinition signal;
}
```

This is preferable to putting every aggregate value into one unrestricted JSON object when metrics need to be queried historically.

### 10.4 Retention

Recommended tiers:

| Data | Suggested retention |
|---|---:|
| Current projections | Indefinite |
| Health transitions | Indefinite or business-defined |
| Detailed metric snapshots | 3–12 months |
| Periodic rollups | Multi-year |
| Raw telemetry | Independent telemetry retention |

Long-term rollups can store hourly or daily:

```text
minimum severity
maximum severity
time in each severity
offline duration
intervention duration
average health score
```

---

## 11. Notifications without the rules engine

The health subsystem can notify interested parties through a typed subscription and delivery mechanism.

### Notification delivery flow

<p align="center">
  <img alt="Architecture flow diagram 6" src="data:image/svg+xml;base64,PCEtLSBHZW5lcmF0ZWQgYnkgZ3JhcGh2aXogdmVyc2lvbiAyLjQyLjQgKDApCiAtLT4KPCEtLSBUaXRsZTogRyBQYWdlczogMSAtLT4KPHN2ZyB3aWR0aD0iODMwcHQiIGhlaWdodD0iNjY3cHQiCiB2aWV3Qm94PSIwLjAwIDAuMDAgODMwLjAwIDY2Ny4wMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayI+CjxnIGlkPSJncmFwaDAiIGNsYXNzPSJncmFwaCIgdHJhbnNmb3JtPSJzY2FsZSgxIDEpIHJvdGF0ZSgwKSB0cmFuc2xhdGUoMTggNjQ5KSI+Cjx0aXRsZT5HPC90aXRsZT4KPCEtLSBIIC0tPgo8ZyBpZD0ibm9kZTEiIGNsYXNzPSJub2RlIj4KPHRpdGxlPkg8L3RpdGxlPgo8cGF0aCBmaWxsPSIjZjhmYWZjIiBzdHJva2U9IiM0YTU1NjgiIHN0cm9rZS13aWR0aD0iMS4yIiBkPSJNNzA1LC02MzFDNzA1LC02MzEgNTIwLC02MzEgNTIwLC02MzEgNTE0LC02MzEgNTA4LC02MjUgNTA4LC02MTkgNTA4LC02MTkgNTA4LC02MDUgNTA4LC02MDUgNTA4LC01OTkgNTE0LC01OTMgNTIwLC01OTMgNTIwLC01OTMgNzA1LC01OTMgNzA1LC01OTMgNzExLC01OTMgNzE3LC01OTkgNzE3LC02MDUgNzE3LC02MDUgNzE3LC02MTkgNzE3LC02MTkgNzE3LC02MjUgNzExLC02MzEgNzA1LC02MzEiLz4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iNjEyLjUiIHk9Ii02MTUuMiIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjExLjAwIiBmaWxsPSIjMWYyOTM3Ij5SZW1vdGVIZWFsdGhDaGFuZ2VkRXZlbnQ8L3RleHQ+Cjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIHg9IjYxMi41IiB5PSItNjAzLjIiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxMS4wMCIgZmlsbD0iIzFmMjkzNyI+b3IgUmVtb3RlR3JvdXBIZWFsdGhDaGFuZ2VkRXZlbnQ8L3RleHQ+CjwvZz4KPCEtLSBPIC0tPgo8ZyBpZD0ibm9kZTIiIGNsYXNzPSJub2RlIj4KPHRpdGxlPk88L3RpdGxlPgo8cGF0aCBmaWxsPSIjZjhmYWZjIiBzdHJva2U9IiM0YTU1NjgiIHN0cm9rZS13aWR0aD0iMS4yIiBkPSJNNjYyLjUsLTU0OEM2NjIuNSwtNTQ4IDU2Mi41LC01NDggNTYyLjUsLTU0OCA1NTYuNSwtNTQ4IDU1MC41LC01NDIgNTUwLjUsLTUzNiA1NTAuNSwtNTM2IDU1MC41LC01MjQgNTUwLjUsLTUyNCA1NTAuNSwtNTE4IDU1Ni41LC01MTIgNTYyLjUsLTUxMiA1NjIuNSwtNTEyIDY2Mi41LC01MTIgNjYyLjUsLTUxMiA2NjguNSwtNTEyIDY3NC41LC01MTggNjc0LjUsLTUyNCA2NzQuNSwtNTI0IDY3NC41LC01MzYgNjc0LjUsLTUzNiA2NzQuNSwtNTQyIDY2OC41LC01NDggNjYyLjUsLTU0OCIvPgo8dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiB4PSI2MTIuNSIgeT0iLTUyNy4yIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTEuMDAiIGZpbGw9IiMxZjI5MzciPlRyYW5zYWN0aW9uYWwgb3V0Ym94PC90ZXh0Pgo8L2c+CjwhLS0gSCYjNDU7Jmd0O08gLS0+CjxnIGlkPSJlZGdlMSIgY2xhc3M9ImVkZ2UiPgo8dGl0bGU+SCYjNDU7Jmd0O088L3RpdGxlPgo8cGF0aCBmaWxsPSJub25lIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBkPSJNNjEyLjUsLTU5Mi45OEM2MTIuNSwtNTkyLjk4IDYxMi41LC01NTUuNTcgNjEyLjUsLTU1NS41NyIvPgo8cG9seWdvbiBmaWxsPSIjNjQ3NDhiIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBwb2ludHM9IjYxNS4xMywtNTU1LjU3IDYxMi41LC01NDguMDcgNjA5Ljg4LC01NTUuNTcgNjE1LjEzLC01NTUuNTciLz4KPC9nPgo8IS0tIFAgLS0+CjxnIGlkPSJub2RlMyIgY2xhc3M9Im5vZGUiPgo8dGl0bGU+UDwvdGl0bGU+CjxwYXRoIGZpbGw9IiNmOGZhZmMiIHN0cm9rZT0iIzRhNTU2OCIgc3Ryb2tlLXdpZHRoPSIxLjIiIGQ9Ik02NTMuNSwtNDY3QzY1My41LC00NjcgNTcxLjUsLTQ2NyA1NzEuNSwtNDY3IDU2NS41LC00NjcgNTU5LjUsLTQ2MSA1NTkuNSwtNDU1IDU1OS41LC00NTUgNTU5LjUsLTQ0MyA1NTkuNSwtNDQzIDU1OS41LC00MzcgNTY1LjUsLTQzMSA1NzEuNSwtNDMxIDU3MS41LC00MzEgNjUzLjUsLTQzMSA2NTMuNSwtNDMxIDY1OS41LC00MzEgNjY1LjUsLTQzNyA2NjUuNSwtNDQzIDY2NS41LC00NDMgNjY1LjUsLTQ1NSA2NjUuNSwtNDU1IDY2NS41LC00NjEgNjU5LjUsLTQ2NyA2NTMuNSwtNDY3Ii8+Cjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIHg9IjYxMi41IiB5PSItNDQ2LjIiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxMS4wMCIgZmlsbD0iIzFmMjkzNyI+T3V0Ym94IHB1Ymxpc2hlcjwvdGV4dD4KPC9nPgo8IS0tIE8mIzQ1OyZndDtQIC0tPgo8ZyBpZD0iZWRnZTIiIGNsYXNzPSJlZGdlIj4KPHRpdGxlPk8mIzQ1OyZndDtQPC90aXRsZT4KPHBhdGggZmlsbD0ibm9uZSIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgZD0iTTYxMi41LC01MTEuNjJDNjEyLjUsLTUxMS42MiA2MTIuNSwtNDc0LjUzIDYxMi41LC00NzQuNTMiLz4KPHBvbHlnb24gZmlsbD0iIzY0NzQ4YiIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgcG9pbnRzPSI2MTUuMTMsLTQ3NC41MyA2MTIuNSwtNDY3LjAzIDYwOS44OCwtNDc0LjUzIDYxNS4xMywtNDc0LjUzIi8+CjwvZz4KPCEtLSBTIC0tPgo8ZyBpZD0ibm9kZTQiIGNsYXNzPSJub2RlIj4KPHRpdGxlPlM8L3RpdGxlPgo8cGF0aCBmaWxsPSIjZjhmYWZjIiBzdHJva2U9IiM0YTU1NjgiIHN0cm9rZS13aWR0aD0iMS4yIiBkPSJNNTAwLjUsLTM4NkM1MDAuNSwtMzg2IDMwMC41LC0zODYgMzAwLjUsLTM4NiAyOTQuNSwtMzg2IDI4OC41LC0zODAgMjg4LjUsLTM3NCAyODguNSwtMzc0IDI4OC41LC0zNjIgMjg4LjUsLTM2MiAyODguNSwtMzU2IDI5NC41LC0zNTAgMzAwLjUsLTM1MCAzMDAuNSwtMzUwIDUwMC41LC0zNTAgNTAwLjUsLTM1MCA1MDYuNSwtMzUwIDUxMi41LC0zNTYgNTEyLjUsLTM2MiA1MTIuNSwtMzYyIDUxMi41LC0zNzQgNTEyLjUsLTM3NCA1MTIuNSwtMzgwIDUwNi41LC0zODYgNTAwLjUsLTM4NiIvPgo8dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiB4PSI0MDAuNSIgeT0iLTM2NS4yIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTEuMDAiIGZpbGw9IiMxZjI5MzciPlJlc29sdmUgSGVhbHRoRXZlbnRTdWJzY3JpcHRpb24gcmVjb3JkczwvdGV4dD4KPC9nPgo8IS0tIFAmIzQ1OyZndDtTIC0tPgo8ZyBpZD0iZWRnZTMiIGNsYXNzPSJlZGdlIj4KPHRpdGxlPlAmIzQ1OyZndDtTPC90aXRsZT4KPHBhdGggZmlsbD0ibm9uZSIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgZD0iTTU3MC43NSwtNDMwLjg0QzU3MC43NSwtNDA4LjY1IDU3MC43NSwtMzc0IDU3MC43NSwtMzc0IDU3MC43NSwtMzc0IDUyMC4xNCwtMzc0IDUyMC4xNCwtMzc0Ii8+Cjxwb2x5Z29uIGZpbGw9IiM2NDc0OGIiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIHBvaW50cz0iNTIwLjE0LC0zNzEuMzggNTEyLjY0LC0zNzQgNTIwLjE0LC0zNzYuNjMgNTIwLjE0LC0zNzEuMzgiLz4KPC9nPgo8IS0tIEQxIC0tPgo8ZyBpZD0ibm9kZTUiIGNsYXNzPSJub2RlIj4KPHRpdGxlPkQxPC90aXRsZT4KPHBhdGggZmlsbD0iI2Y4ZmFmYyIgc3Ryb2tlPSIjNGE1NTY4IiBzdHJva2Utd2lkdGg9IjEuMiIgZD0iTTEzNSwtMzA1QzEzNSwtMzA1IDEyLC0zMDUgMTIsLTMwNSA2LC0zMDUgMCwtMjk5IDAsLTI5MyAwLC0yOTMgMCwtMjgxIDAsLTI4MSAwLC0yNzUgNiwtMjY5IDEyLC0yNjkgMTIsLTI2OSAxMzUsLTI2OSAxMzUsLTI2OSAxNDEsLTI2OSAxNDcsLTI3NSAxNDcsLTI4MSAxNDcsLTI4MSAxNDcsLTI5MyAxNDcsLTI5MyAxNDcsLTI5OSAxNDEsLTMwNSAxMzUsLTMwNSIvPgo8dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiB4PSI3My41IiB5PSItMjg0LjIiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxMS4wMCIgZmlsbD0iIzFmMjkzNyI+SW4mIzQ1O3Byb2Nlc3MgU3ByaW5nIGxpc3RlbmVyPC90ZXh0Pgo8L2c+CjwhLS0gUyYjNDU7Jmd0O0QxIC0tPgo8ZyBpZD0iZWRnZTQiIGNsYXNzPSJlZGdlIj4KPHRpdGxlPlMmIzQ1OyZndDtEMTwvdGl0bGU+CjxwYXRoIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIGQ9Ik0yODguNDIsLTM2OEMxOTMuNzYsLTM2OCA3My41LC0zNjggNzMuNSwtMzY4IDczLjUsLTM2OCA3My41LC0zMTIuODggNzMuNSwtMzEyLjg4Ii8+Cjxwb2x5Z29uIGZpbGw9IiM2NDc0OGIiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIHBvaW50cz0iNzYuMTMsLTMxMi44OCA3My41LC0zMDUuMzggNzAuODgsLTMxMi44OCA3Ni4xMywtMzEyLjg4Ii8+CjwvZz4KPCEtLSBEMiAtLT4KPGcgaWQ9Im5vZGU2IiBjbGFzcz0ibm9kZSI+Cjx0aXRsZT5EMjwvdGl0bGU+CjxwYXRoIGZpbGw9IiNmOGZhZmMiIHN0cm9rZT0iIzRhNTU2OCIgc3Ryb2tlLXdpZHRoPSIxLjIiIGQ9Ik0yNzkuNSwtMzA1QzI3OS41LC0zMDUgMTkxLjUsLTMwNSAxOTEuNSwtMzA1IDE4NS41LC0zMDUgMTc5LjUsLTI5OSAxNzkuNSwtMjkzIDE3OS41LC0yOTMgMTc5LjUsLTI4MSAxNzkuNSwtMjgxIDE3OS41LC0yNzUgMTg1LjUsLTI2OSAxOTEuNSwtMjY5IDE5MS41LC0yNjkgMjc5LjUsLTI2OSAyNzkuNSwtMjY5IDI4NS41LC0yNjkgMjkxLjUsLTI3NSAyOTEuNSwtMjgxIDI5MS41LC0yODEgMjkxLjUsLTI5MyAyOTEuNSwtMjkzIDI5MS41LC0yOTkgMjg1LjUsLTMwNSAyNzkuNSwtMzA1Ii8+Cjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIHg9IjIzNS41IiB5PSItMjg0LjIiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxMS4wMCIgZmlsbD0iIzFmMjkzNyI+V2ViU29ja2V0IC8gU1NFPC90ZXh0Pgo8L2c+CjwhLS0gUyYjNDU7Jmd0O0QyIC0tPgo8ZyBpZD0iZWRnZTUiIGNsYXNzPSJlZGdlIj4KPHRpdGxlPlMmIzQ1OyZndDtEMjwvdGl0bGU+CjxwYXRoIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIGQ9Ik0zMDYsLTM0OS42MkMzMDYsLTMyNS43MiAzMDYsLTI4NyAzMDYsLTI4NyAzMDYsLTI4NyAyOTkuMjMsLTI4NyAyOTkuMjMsLTI4NyIvPgo8cG9seWdvbiBmaWxsPSIjNjQ3NDhiIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBwb2ludHM9IjI5OS4yMywtMjg0LjM4IDI5MS43MywtMjg3IDI5OS4yMywtMjg5LjYzIDI5OS4yMywtMjg0LjM4Ii8+CjwvZz4KPCEtLSBEMyAtLT4KPGcgaWQ9Im5vZGU3IiBjbGFzcz0ibm9kZSI+Cjx0aXRsZT5EMzwvdGl0bGU+CjxwYXRoIGZpbGw9IiNmOGZhZmMiIHN0cm9rZT0iIzRhNTU2OCIgc3Ryb2tlLXdpZHRoPSIxLjIiIGQ9Ik0zODEuNSwtMzA1QzM4MS41LC0zMDUgMzM1LjUsLTMwNSAzMzUuNSwtMzA1IDMyOS41LC0zMDUgMzIzLjUsLTI5OSAzMjMuNSwtMjkzIDMyMy41LC0yOTMgMzIzLjUsLTI4MSAzMjMuNSwtMjgxIDMyMy41LC0yNzUgMzI5LjUsLTI2OSAzMzUuNSwtMjY5IDMzNS41LC0yNjkgMzgxLjUsLTI2OSAzODEuNSwtMjY5IDM4Ny41LC0yNjkgMzkzLjUsLTI3NSAzOTMuNSwtMjgxIDM5My41LC0yODEgMzkzLjUsLTI5MyAzOTMuNSwtMjkzIDM5My41LC0yOTkgMzg3LjUsLTMwNSAzODEuNSwtMzA1Ii8+Cjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIHg9IjM1OC41IiB5PSItMjg0LjIiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxMS4wMCIgZmlsbD0iIzFmMjkzNyI+V2ViaG9vazwvdGV4dD4KPC9nPgo8IS0tIFMmIzQ1OyZndDtEMyAtLT4KPGcgaWQ9ImVkZ2U2IiBjbGFzcz0iZWRnZSI+Cjx0aXRsZT5TJiM0NTsmZ3Q7RDM8L3RpdGxlPgo8cGF0aCBmaWxsPSJub25lIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBkPSJNMzU4LjUsLTM0OS42MkMzNTguNSwtMzQ5LjYyIDM1OC41LC0zMTIuNTMgMzU4LjUsLTMxMi41MyIvPgo8cG9seWdvbiBmaWxsPSIjNjQ3NDhiIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBwb2ludHM9IjM2MS4xMywtMzEyLjUzIDM1OC41LC0zMDUuMDMgMzU1Ljg4LC0zMTIuNTMgMzYxLjEzLC0zMTIuNTMiLz4KPC9nPgo8IS0tIEQ0IC0tPgo8ZyBpZD0ibm9kZTgiIGNsYXNzPSJub2RlIj4KPHRpdGxlPkQ0PC90aXRsZT4KPHBhdGggZmlsbD0iI2Y4ZmFmYyIgc3Ryb2tlPSIjNGE1NTY4IiBzdHJva2Utd2lkdGg9IjEuMiIgZD0iTTUzNy41LC0zMDVDNTM3LjUsLTMwNSA0MzcuNSwtMzA1IDQzNy41LC0zMDUgNDMxLjUsLTMwNSA0MjUuNSwtMjk5IDQyNS41LC0yOTMgNDI1LjUsLTI5MyA0MjUuNSwtMjgxIDQyNS41LC0yODEgNDI1LjUsLTI3NSA0MzEuNSwtMjY5IDQzNy41LC0yNjkgNDM3LjUsLTI2OSA1MzcuNSwtMjY5IDUzNy41LC0yNjkgNTQzLjUsLTI2OSA1NDkuNSwtMjc1IDU0OS41LC0yODEgNTQ5LjUsLTI4MSA1NDkuNSwtMjkzIDU0OS41LC0yOTMgNTQ5LjUsLTI5OSA1NDMuNSwtMzA1IDUzNy41LC0zMDUiLz4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iNDg3LjUiIHk9Ii0yODQuMiIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjExLjAwIiBmaWxsPSIjMWYyOTM3Ij5FbWFpbCAvIHB1c2ggYWRhcHRlcjwvdGV4dD4KPC9nPgo8IS0tIFMmIzQ1OyZndDtENCAtLT4KPGcgaWQ9ImVkZ2U3IiBjbGFzcz0iZWRnZSI+Cjx0aXRsZT5TJiM0NTsmZ3Q7RDQ8L3RpdGxlPgo8cGF0aCBmaWxsPSJub25lIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBkPSJNNDY5LC0zNDkuNjJDNDY5LC0zNDkuNjIgNDY5LC0zMTIuNTMgNDY5LC0zMTIuNTMiLz4KPHBvbHlnb24gZmlsbD0iIzY0NzQ4YiIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgcG9pbnRzPSI0NzEuNjMsLTMxMi41MyA0NjksLTMwNS4wMyA0NjYuMzgsLTMxMi41MyA0NzEuNjMsLTMxMi41MyIvPgo8L2c+CjwhLS0gRDUgLS0+CjxnIGlkPSJub2RlOSIgY2xhc3M9Im5vZGUiPgo8dGl0bGU+RDU8L3RpdGxlPgo8cGF0aCBmaWxsPSIjZjhmYWZjIiBzdHJva2U9IiM0YTU1NjgiIHN0cm9rZS13aWR0aD0iMS4yIiBkPSJNNjc3LC0zMDVDNjc3LC0zMDUgNTk0LC0zMDUgNTk0LC0zMDUgNTg4LC0zMDUgNTgyLC0yOTkgNTgyLC0yOTMgNTgyLC0yOTMgNTgyLC0yODEgNTgyLC0yODEgNTgyLC0yNzUgNTg4LC0yNjkgNTk0LC0yNjkgNTk0LC0yNjkgNjc3LC0yNjkgNjc3LC0yNjkgNjgzLC0yNjkgNjg5LC0yNzUgNjg5LC0yODEgNjg5LC0yODEgNjg5LC0yOTMgNjg5LC0yOTMgNjg5LC0yOTkgNjgzLC0zMDUgNjc3LC0zMDUiLz4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iNjM1LjUiIHk9Ii0yODQuMiIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjExLjAwIiBmaWxsPSIjMWYyOTM3Ij5JbnRlcm5hbCBtZXNzYWdlPC90ZXh0Pgo8L2c+CjwhLS0gUyYjNDU7Jmd0O0Q1IC0tPgo8ZyBpZD0iZWRnZTgiIGNsYXNzPSJlZGdlIj4KPHRpdGxlPlMmIzQ1OyZndDtENTwvdGl0bGU+CjxwYXRoIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIGQ9Ik01MTIuNzgsLTM2MkM1NjguMzcsLTM2MiA2MjMuNzUsLTM2MiA2MjMuNzUsLTM2MiA2MjMuNzUsLTM2MiA2MjMuNzUsLTMxMi42NiA2MjMuNzUsLTMxMi42NiIvPgo8cG9seWdvbiBmaWxsPSIjNjQ3NDhiIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBwb2ludHM9IjYyNi4zOCwtMzEyLjY2IDYyMy43NSwtMzA1LjE2IDYyMS4xMywtMzEyLjY2IDYyNi4zOCwtMzEyLjY2Ii8+CjwvZz4KPCEtLSBSIC0tPgo8ZyBpZD0ibm9kZTEwIiBjbGFzcz0ibm9kZSI+Cjx0aXRsZT5SPC90aXRsZT4KPHBhdGggZmlsbD0iI2Y4ZmFmYyIgc3Ryb2tlPSIjNGE1NTY4IiBzdHJva2Utd2lkdGg9IjEuMiIgZD0iTTQ1Ni41LC0yMjRDNDU2LjUsLTIyNCAzMzIuNSwtMjI0IDMzMi41LC0yMjQgMzI2LjUsLTIyNCAzMjAuNSwtMjE4IDMyMC41LC0yMTIgMzIwLjUsLTIxMiAzMjAuNSwtMjAwIDMyMC41LC0yMDAgMzIwLjUsLTE5NCAzMjYuNSwtMTg4IDMzMi41LC0xODggMzMyLjUsLTE4OCA0NTYuNSwtMTg4IDQ1Ni41LC0xODggNDYyLjUsLTE4OCA0NjguNSwtMTk0IDQ2OC41LC0yMDAgNDY4LjUsLTIwMCA0NjguNSwtMjEyIDQ2OC41LC0yMTIgNDY4LjUsLTIxOCA0NjIuNSwtMjI0IDQ1Ni41LC0yMjQiLz4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iMzk0LjUiIHk9Ii0yMDMuMiIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjExLjAwIiBmaWxsPSIjMWYyOTM3Ij5IZWFsdGhOb3RpZmljYXRpb25EZWxpdmVyeTwvdGV4dD4KPC9nPgo8IS0tIEQxJiM0NTsmZ3Q7UiAtLT4KPGcgaWQ9ImVkZ2U5IiBjbGFzcz0iZWRnZSI+Cjx0aXRsZT5EMSYjNDU7Jmd0O1I8L3RpdGxlPgo8cGF0aCBmaWxsPSJub25lIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBkPSJNNzMuNSwtMjY4Ljk3QzczLjUsLTI0My40OSA3My41LC0yMDAgNzMuNSwtMjAwIDczLjUsLTIwMCAzMTIuOTUsLTIwMCAzMTIuOTUsLTIwMCIvPgo8cG9seWdvbiBmaWxsPSIjNjQ3NDhiIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBwb2ludHM9IjMxMi45NSwtMjAyLjYzIDMyMC40NSwtMjAwIDMxMi45NSwtMTk3LjM4IDMxMi45NSwtMjAyLjYzIi8+CjwvZz4KPCEtLSBEMiYjNDU7Jmd0O1IgLS0+CjxnIGlkPSJlZGdlMTAiIGNsYXNzPSJlZGdlIj4KPHRpdGxlPkQyJiM0NTsmZ3Q7UjwvdGl0bGU+CjxwYXRoIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIGQ9Ik0yMzUuNSwtMjY4Ljg0QzIzNS41LC0yNDYuNjUgMjM1LjUsLTIxMiAyMzUuNSwtMjEyIDIzNS41LC0yMTIgMzEyLjYyLC0yMTIgMzEyLjYyLC0yMTIiLz4KPHBvbHlnb24gZmlsbD0iIzY0NzQ4YiIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgcG9pbnRzPSIzMTIuNjIsLTIxNC42MyAzMjAuMTIsLTIxMiAzMTIuNjIsLTIwOS4zOCAzMTIuNjIsLTIxNC42MyIvPgo8L2c+CjwhLS0gRDMmIzQ1OyZndDtSIC0tPgo8ZyBpZD0iZWRnZTExIiBjbGFzcz0iZWRnZSI+Cjx0aXRsZT5EMyYjNDU7Jmd0O1I8L3RpdGxlPgo8cGF0aCBmaWxsPSJub25lIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBkPSJNMzU4LjUsLTI2OC42MkMzNTguNSwtMjY4LjYyIDM1OC41LC0yMzEuNTMgMzU4LjUsLTIzMS41MyIvPgo8cG9seWdvbiBmaWxsPSIjNjQ3NDhiIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBwb2ludHM9IjM2MS4xMywtMjMxLjUzIDM1OC41LC0yMjQuMDMgMzU1Ljg4LC0yMzEuNTMgMzYxLjEzLC0yMzEuNTMiLz4KPC9nPgo8IS0tIEQ0JiM0NTsmZ3Q7UiAtLT4KPGcgaWQ9ImVkZ2UxMiIgY2xhc3M9ImVkZ2UiPgo8dGl0bGU+RDQmIzQ1OyZndDtSPC90aXRsZT4KPHBhdGggZmlsbD0ibm9uZSIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgZD0iTTQ0NywtMjY4LjYyQzQ0NywtMjY4LjYyIDQ0NywtMjMxLjUzIDQ0NywtMjMxLjUzIi8+Cjxwb2x5Z29uIGZpbGw9IiM2NDc0OGIiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIHBvaW50cz0iNDQ5LjYzLC0yMzEuNTMgNDQ3LC0yMjQuMDMgNDQ0LjM4LC0yMzEuNTMgNDQ5LjYzLC0yMzEuNTMiLz4KPC9nPgo8IS0tIEQ1JiM0NTsmZ3Q7UiAtLT4KPGcgaWQ9ImVkZ2UxMyIgY2xhc3M9ImVkZ2UiPgo8dGl0bGU+RDUmIzQ1OyZndDtSPC90aXRsZT4KPHBhdGggZmlsbD0ibm9uZSIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgZD0iTTU4Ny43NSwtMjY4LjYyQzU4Ny43NSwtMjQ0LjcyIDU4Ny43NSwtMjA2IDU4Ny43NSwtMjA2IDU4Ny43NSwtMjA2IDQ3Ni4xNiwtMjA2IDQ3Ni4xNiwtMjA2Ii8+Cjxwb2x5Z29uIGZpbGw9IiM2NDc0OGIiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIHBvaW50cz0iNDc2LjE2LC0yMDMuMzggNDY4LjY2LC0yMDYgNDc2LjE2LC0yMDguNjMgNDc2LjE2LC0yMDMuMzgiLz4KPC9nPgo8IS0tIE9LIC0tPgo8ZyBpZD0ibm9kZTExIiBjbGFzcz0ibm9kZSI+Cjx0aXRsZT5PSzwvdGl0bGU+Cjxwb2x5Z29uIGZpbGw9IiNmZmZhZjAiIHN0cm9rZT0iIzlhNmIxZiIgc3Ryb2tlLXdpZHRoPSIxLjIiIHBvaW50cz0iNTE3LjUsLTE0MyA0NDEuNSwtMTE3IDUxNy41LC05MSA1OTMuNSwtMTE3IDUxNy41LC0xNDMiLz4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iNTE3LjUiIHk9Ii0xMTQuMiIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjExLjAwIiBmaWxsPSIjMWYyOTM3Ij5EZWxpdmVyZWQ/PC90ZXh0Pgo8L2c+CjwhLS0gUiYjNDU7Jmd0O09LIC0tPgo8ZyBpZD0iZWRnZTE0IiBjbGFzcz0iZWRnZSI+Cjx0aXRsZT5SJiM0NTsmZ3Q7T0s8L3RpdGxlPgo8cGF0aCBmaWxsPSJub25lIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBkPSJNNDU1LC0xODcuOTlDNDU1LC0xODcuOTkgNDU1LC0xMjkuMTcgNDU1LC0xMjkuMTciLz4KPHBvbHlnb24gZmlsbD0iIzY0NzQ4YiIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgcG9pbnRzPSI0NTcuNjMsLTEyOS4xNyA0NTUsLTEyMS42NyA0NTIuMzgsLTEyOS4xNyA0NTcuNjMsLTEyOS4xNyIvPgo8L2c+CjwhLS0gRE9ORSAtLT4KPGcgaWQ9Im5vZGUxMiIgY2xhc3M9Im5vZGUiPgo8dGl0bGU+RE9ORTwvdGl0bGU+CjxwYXRoIGZpbGw9IiNmOGZhZmMiIHN0cm9rZT0iIzRhNTU2OCIgc3Ryb2tlLXdpZHRoPSIxLjIiIGQ9Ik01NTMsLTM2QzU1MywtMzYgNDgyLC0zNiA0ODIsLTM2IDQ3NiwtMzYgNDcwLC0zMCA0NzAsLTI0IDQ3MCwtMjQgNDcwLC0xMiA0NzAsLTEyIDQ3MCwtNiA0NzYsMCA0ODIsMCA0ODIsMCA1NTMsMCA1NTMsMCA1NTksMCA1NjUsLTYgNTY1LC0xMiA1NjUsLTEyIDU2NSwtMjQgNTY1LC0yNCA1NjUsLTMwIDU1OSwtMzYgNTUzLC0zNiIvPgo8dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiB4PSI1MTcuNSIgeT0iLTE1LjIiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxMS4wMCIgZmlsbD0iIzFmMjkzNyI+TWFyayBkZWxpdmVyZWQ8L3RleHQ+CjwvZz4KPCEtLSBPSyYjNDU7Jmd0O0RPTkUgLS0+CjxnIGlkPSJlZGdlMTUiIGNsYXNzPSJlZGdlIj4KPHRpdGxlPk9LJiM0NTsmZ3Q7RE9ORTwvdGl0bGU+CjxwYXRoIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIGQ9Ik01MTcuNSwtOTAuOTlDNTE3LjUsLTkwLjk5IDUxNy41LC00My41MyA1MTcuNSwtNDMuNTMiLz4KPHBvbHlnb24gZmlsbD0iIzY0NzQ4YiIgc3Ryb2tlPSIjNjQ3NDhiIiBzdHJva2Utd2lkdGg9IjEuMSIgcG9pbnRzPSI1MjAuMTMsLTQzLjUzIDUxNy41LC0zNi4wMyA1MTQuODgsLTQzLjUzIDUyMC4xMywtNDMuNTMiLz4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iNTI2IiB5PSItNjEiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxMC4wMCIgZmlsbD0iIzMzNDE1NSI+WWVzPC90ZXh0Pgo8L2c+CjwhLS0gUkVUUlkgLS0+CjxnIGlkPSJub2RlMTMiIGNsYXNzPSJub2RlIj4KPHRpdGxlPlJFVFJZPC90aXRsZT4KPHBhdGggZmlsbD0iI2Y4ZmFmYyIgc3Ryb2tlPSIjNGE1NTY4IiBzdHJva2Utd2lkdGg9IjEuMiIgZD0iTTc4MiwtMzZDNzgyLC0zNiA2NjcsLTM2IDY2NywtMzYgNjYxLC0zNiA2NTUsLTMwIDY1NSwtMjQgNjU1LC0yNCA2NTUsLTEyIDY1NSwtMTIgNjU1LC02IDY2MSwwIDY2NywwIDY2NywwIDc4MiwwIDc4MiwwIDc4OCwwIDc5NCwtNiA3OTQsLTEyIDc5NCwtMTIgNzk0LC0yNCA3OTQsLTI0IDc5NCwtMzAgNzg4LC0zNiA3ODIsLTM2Ii8+Cjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIHg9IjcyNC41IiB5PSItMTUuMiIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjExLjAwIiBmaWxsPSIjMWYyOTM3Ij5TY2hlZHVsZSBib3VuZGVkIHJldHJ5PC90ZXh0Pgo8L2c+CjwhLS0gT0smIzQ1OyZndDtSRVRSWSAtLT4KPGcgaWQ9ImVkZ2UxNiIgY2xhc3M9ImVkZ2UiPgo8dGl0bGU+T0smIzQ1OyZndDtSRVRSWTwvdGl0bGU+CjxwYXRoIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIGQ9Ik01NzkuMjUsLTExMS45NUM1NzkuMjUsLTkxLjc4IDU3OS4yNSwtMTggNTc5LjI1LC0xOCA1NzkuMjUsLTE4IDY0Ny4zNSwtMTggNjQ3LjM1LC0xOCIvPgo8cG9seWdvbiBmaWxsPSIjNjQ3NDhiIiBzdHJva2U9IiM2NDc0OGIiIHN0cm9rZS13aWR0aD0iMS4xIiBwb2ludHM9IjY0Ny4zNSwtMjAuNjMgNjU0Ljg1LC0xOCA2NDcuMzUsLTE1LjM4IDY0Ny4zNSwtMjAuNjMiLz4KPHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iNjUxIiB5PSItNjEiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxMC4wMCIgZmlsbD0iIzMzNDE1NSI+Tm88L3RleHQ+CjwvZz4KPCEtLSBSRVRSWSYjNDU7Jmd0O1AgLS0+CjxnIGlkPSJlZGdlMTciIGNsYXNzPSJlZGdlIj4KPHRpdGxlPlJFVFJZJiM0NTsmZ3Q7UDwvdGl0bGU+CjxwYXRoIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIGQ9Ik03MDMsLTM2LjI0QzcwMywtMTE4LjI4IDcwMywtNDQ5IDcwMywtNDQ5IDcwMywtNDQ5IDY3My4xNywtNDQ5IDY3My4xNywtNDQ5Ii8+Cjxwb2x5Z29uIGZpbGw9IiM2NDc0OGIiIHN0cm9rZT0iIzY0NzQ4YiIgc3Ryb2tlLXdpZHRoPSIxLjEiIHBvaW50cz0iNjczLjE3LC00NDYuMzggNjY1LjY3LC00NDkgNjczLjE3LC00NTEuNjMgNjczLjE3LC00NDYuMzgiLz4KPC9nPgo8L2c+Cjwvc3ZnPgo=" style="max-width: 100%; height: auto;" />
</p>

<details>
<summary>Mermaid source for diagram 6</summary>

```text
flowchart TB
    H["RemoteHealthChangedEvent<br/>or RemoteGroupHealthChangedEvent"] --> O["Transactional outbox"]
    O --> P["Outbox publisher"]
    P --> S["Resolve HealthEventSubscription records"]

    S --> D1["In-process Spring listener"]
    S --> D2["WebSocket / SSE"]
    S --> D3["Webhook"]
    S --> D4["Email / push adapter"]
    S --> D5["Internal message"]

    D1 --> R["HealthNotificationDelivery"]
    D2 --> R
    D3 --> R
    D4 --> R
    D5 --> R

    R --> OK{"Delivered?"}
    OK -- "Yes" --> DONE["Mark delivered"]
    OK -- "No" --> RETRY["Schedule bounded retry"]
    RETRY --> P
```

</details>

### 11.1 Spring event consumers

Other plugins can listen directly:

```java
@Component
public class MaintenanceHealthListener {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRemoteHealthChanged(RemoteHealthChangedEvent event) {
        // create or update maintenance work item
    }
}
```

```java
@Component
public class DashboardHealthListener {

    @EventListener
    public void onGroupHealthChanged(RemoteGroupHealthChangedEvent event) {
        // update WebSocket/SSE topic or invalidate cache
    }
}
```

This is appropriate for in-process plugin integrations.

### 11.2 Persistent subscriptions

For configurable notifications, add:

```java
@Entity
public class HealthEventSubscription extends Baseclass {

    @Enumerated(EnumType.STRING)
    private HealthEventSubjectType subjectType;

    private String subjectId;

    @Enumerated(EnumType.STRING)
    private HealthEventType eventType;

    private Integer minimumSeverityValue;
    private Boolean escalationOnly;
    private Boolean interventionOnly;

    @Enumerated(EnumType.STRING)
    private HealthDeliveryChannel channel;

    private String destination;
    private Long reminderIntervalSeconds;

    private Boolean enabled;
}
```

Subject types:

```java
REMOTE,
REMOTE_GROUP,
DEVICE_TYPE,
FLEET_POLICY,
ALL_ACCESSIBLE
```

Delivery channels:

```java
SPRING_EVENT,
WEBHOOK,
WEBSOCKET,
SSE,
EMAIL_ADAPTER,
PUSH_ADAPTER,
INTERNAL_MESSAGE
```

### 11.3 Notification event

The health service publishes a generic durable notification request:

```java
public record HealthNotificationRequestedEvent(
        UUID sourceEventId,
        HealthEventSubjectType subjectType,
        UUID subjectId,
        String severityName,
        Integer severityValue,
        boolean escalation,
        boolean interventionRequired,
        String summary,
        OffsetDateTime occurredAt) {
}
```

A notification dispatcher resolves subscriptions and creates delivery records.

### 11.4 Delivery persistence

```java
@Entity
public class HealthNotificationDelivery extends Baseclass {

    @ManyToOne
    private HealthEventSubscription subscription;

    private UUID sourceEventId;

    @Enumerated(EnumType.STRING)
    private HealthDeliveryStatus status;

    private Integer attemptCount;
    private OffsetDateTime nextAttemptAt;
    private OffsetDateTime deliveredAt;

    private String failureMessage;
}
```

A unique constraint on:

```text
subscription_id + source_event_id
```

prevents duplicate deliveries.

### 11.5 Transactional outbox

Spring events alone are not durable across process failure.

For external notifications, write an outbox row in the same transaction as the health transition:

```java
@Entity
public class HealthEventOutbox extends Baseclass {

    private UUID eventId;
    private String eventType;
    private UUID subjectId;
    private Long subjectVersion;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> payload;

    private OffsetDateTime publishedAt;
    private Integer publishAttempts;
}
```

Flow:

```text
Health transition transaction
    ├─ update current health
    ├─ insert history
    └─ insert outbox event
             ↓
Outbox publisher
             ↓
Spring event / webhook / message broker / WebSocket
```

This preserves delivery without invoking the rules engine.

---

## 12. Recommended processing sequence

<p align="center">
  <img alt="Architecture sequence diagram 7" src="data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxMjAwIiBoZWlnaHQ9IjE0ODQiIHZpZXdCb3g9IjAgMCAxMjAwIDE0ODQiIHJvbGU9ImltZyI+PGRlZnM+PG1hcmtlciBpZD0iYXJyb3ciIG1hcmtlcldpZHRoPSI4IiBtYXJrZXJIZWlnaHQ9IjgiIHJlZlg9IjciIHJlZlk9IjQiIG9yaWVudD0iYXV0byIgbWFya2VyVW5pdHM9InN0cm9rZVdpZHRoIj48cGF0aCBkPSJNMCwwIEw4LDQgTDAsOCB6IiBmaWxsPSIjNDc1NTY5Ii8+PC9tYXJrZXI+PC9kZWZzPjxyZWN0IHg9IjAiIHk9IjAiIHdpZHRoPSIxMDAlIiBoZWlnaHQ9IjEwMCUiIGZpbGw9IndoaXRlIiBmaWxsLW9wYWNpdHk9IjAiLz48cmVjdCB4PSIxLjAiIHk9IjIyIiB3aWR0aD0iMTQ4IiBoZWlnaHQ9IjUyIiByeD0iOCIgZmlsbD0iI2Y4ZmFmYyIgc3Ryb2tlPSIjNGE1NTY4IiBzdHJva2Utd2lkdGg9IjEuMiIvPjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIGZvbnQtZmFtaWx5PSJBcmlhbCwgc2Fucy1zZXJpZiIgZm9udC1zaXplPSIxMiIgZm9udC13ZWlnaHQ9ImJvbGQiIGZpbGw9IiMxZjI5MzciPjx0c3BhbiB4PSI3NS4wIiB5PSI0OS4wIj5TdGF0ZSBpbmdlc3Rpb248L3RzcGFuPjwvdGV4dD48bGluZSB4MT0iNzUiIHkxPSI3NCIgeDI9Ijc1IiB5Mj0iMTQxMiIgc3Ryb2tlPSIjOTRhM2I4IiBzdHJva2Utd2lkdGg9IjEuMSIgc3Ryb2tlLWRhc2hhcnJheT0iNSA1Ii8+PHJlY3QgeD0iMTc2LjAiIHk9IjIyIiB3aWR0aD0iMTQ4IiBoZWlnaHQ9IjUyIiByeD0iOCIgZmlsbD0iI2Y4ZmFmYyIgc3Ryb2tlPSIjNGE1NTY4IiBzdHJva2Utd2lkdGg9IjEuMiIvPjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIGZvbnQtZmFtaWx5PSJBcmlhbCwgc2Fucy1zZXJpZiIgZm9udC1zaXplPSIxMiIgZm9udC13ZWlnaHQ9ImJvbGQiIGZpbGw9IiMxZjI5MzciPjx0c3BhbiB4PSIyNTAuMCIgeT0iNDkuMCI+RGF0YWJhc2U8L3RzcGFuPjwvdGV4dD48bGluZSB4MT0iMjUwIiB5MT0iNzQiIHgyPSIyNTAiIHkyPSIxNDEyIiBzdHJva2U9IiM5NGEzYjgiIHN0cm9rZS13aWR0aD0iMS4xIiBzdHJva2UtZGFzaGFycmF5PSI1IDUiLz48cmVjdCB4PSIzNTEuMCIgeT0iMjIiIHdpZHRoPSIxNDgiIGhlaWdodD0iNTIiIHJ4PSI4IiBmaWxsPSIjZjhmYWZjIiBzdHJva2U9IiM0YTU1NjgiIHN0cm9rZS13aWR0aD0iMS4yIi8+PHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgZm9udC1mYW1pbHk9IkFyaWFsLCBzYW5zLXNlcmlmIiBmb250LXNpemU9IjEyIiBmb250LXdlaWdodD0iYm9sZCIgZmlsbD0iIzFmMjkzNyI+PHRzcGFuIHg9IjQyNS4wIiB5PSI0OS4wIj5TcHJpbmcgZXZlbnQgYnVzPC90c3Bhbj48L3RleHQ+PGxpbmUgeDE9IjQyNSIgeTE9Ijc0IiB4Mj0iNDI1IiB5Mj0iMTQxMiIgc3Ryb2tlPSIjOTRhM2I4IiBzdHJva2Utd2lkdGg9IjEuMSIgc3Ryb2tlLWRhc2hhcnJheT0iNSA1Ii8+PHJlY3QgeD0iNTI2LjAiIHk9IjIyIiB3aWR0aD0iMTQ4IiBoZWlnaHQ9IjUyIiByeD0iOCIgZmlsbD0iI2Y4ZmFmYyIgc3Ryb2tlPSIjNGE1NTY4IiBzdHJva2Utd2lkdGg9IjEuMiIvPjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIGZvbnQtZmFtaWx5PSJBcmlhbCwgc2Fucy1zZXJpZiIgZm9udC1zaXplPSIxMiIgZm9udC13ZWlnaHQ9ImJvbGQiIGZpbGw9IiMxZjI5MzciPjx0c3BhbiB4PSI2MDAuMCIgeT0iNDEuNSI+UmVtb3RlIGhlYWx0aDwvdHNwYW4+PHRzcGFuIHg9IjYwMC4wIiBkeT0iMTUiPnNlcnZpY2U8L3RzcGFuPjwvdGV4dD48bGluZSB4MT0iNjAwIiB5MT0iNzQiIHgyPSI2MDAiIHkyPSIxNDEyIiBzdHJva2U9IiM5NGEzYjgiIHN0cm9rZS13aWR0aD0iMS4xIiBzdHJva2UtZGFzaGFycmF5PSI1IDUiLz48cmVjdCB4PSI3MDEuMCIgeT0iMjIiIHdpZHRoPSIxNDgiIGhlaWdodD0iNTIiIHJ4PSI4IiBmaWxsPSIjZjhmYWZjIiBzdHJva2U9IiM0YTU1NjgiIHN0cm9rZS13aWR0aD0iMS4yIi8+PHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgZm9udC1mYW1pbHk9IkFyaWFsLCBzYW5zLXNlcmlmIiBmb250LXNpemU9IjEyIiBmb250LXdlaWdodD0iYm9sZCIgZmlsbD0iIzFmMjkzNyI+PHRzcGFuIHg9Ijc3NS4wIiB5PSI0OS4wIj5Hcm91cCBoZWFsdGggc2VydmljZTwvdHNwYW4+PC90ZXh0PjxsaW5lIHgxPSI3NzUiIHkxPSI3NCIgeDI9Ijc3NSIgeTI9IjE0MTIiIHN0cm9rZT0iIzk0YTNiOCIgc3Ryb2tlLXdpZHRoPSIxLjEiIHN0cm9rZS1kYXNoYXJyYXk9IjUgNSIvPjxyZWN0IHg9Ijg3Ni4wIiB5PSIyMiIgd2lkdGg9IjE0OCIgaGVpZ2h0PSI1MiIgcng9IjgiIGZpbGw9IiNmOGZhZmMiIHN0cm9rZT0iIzRhNTU2OCIgc3Ryb2tlLXdpZHRoPSIxLjIiLz48dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBmb250LWZhbWlseT0iQXJpYWwsIHNhbnMtc2VyaWYiIGZvbnQtc2l6ZT0iMTIiIGZvbnQtd2VpZ2h0PSJib2xkIiBmaWxsPSIjMWYyOTM3Ij48dHNwYW4geD0iOTUwLjAiIHk9IjQ5LjAiPkhpc3Rvcnkgc2VydmljZTwvdHNwYW4+PC90ZXh0PjxsaW5lIHgxPSI5NTAiIHkxPSI3NCIgeDI9Ijk1MCIgeTI9IjE0MTIiIHN0cm9rZT0iIzk0YTNiOCIgc3Ryb2tlLXdpZHRoPSIxLjEiIHN0cm9rZS1kYXNoYXJyYXk9IjUgNSIvPjxyZWN0IHg9IjEwNTEuMCIgeT0iMjIiIHdpZHRoPSIxNDgiIGhlaWdodD0iNTIiIHJ4PSI4IiBmaWxsPSIjZjhmYWZjIiBzdHJva2U9IiM0YTU1NjgiIHN0cm9rZS13aWR0aD0iMS4yIi8+PHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgZm9udC1mYW1pbHk9IkFyaWFsLCBzYW5zLXNlcmlmIiBmb250LXNpemU9IjEyIiBmb250LXdlaWdodD0iYm9sZCIgZmlsbD0iIzFmMjkzNyI+PHRzcGFuIHg9IjExMjUuMCIgeT0iNDkuMCI+Tm90aWZpY2F0aW9uIHNlcnZpY2U8L3RzcGFuPjwvdGV4dD48bGluZSB4MT0iMTEyNSIgeTE9Ijc0IiB4Mj0iMTEyNSIgeTI9IjE0MTIiIHN0cm9rZT0iIzk0YTNiOCIgc3Ryb2tlLXdpZHRoPSIxLjEiIHN0cm9rZS1kYXNoYXJyYXk9IjUgNSIvPjxsaW5lIHgxPSI3OSIgeTE9IjExMCIgeDI9IjI0MiIgeTI9IjExMCIgc3Ryb2tlPSIjNDc1NTY5IiBzdHJva2Utd2lkdGg9IjEuMyIgbWFya2VyLWVuZD0idXJsKCNhcnJvdykiLz48dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBmb250LWZhbWlseT0iQXJpYWwsIHNhbnMtc2VyaWYiIGZvbnQtc2l6ZT0iMTEiIGZvbnQtd2VpZ2h0PSJub3JtYWwiIGZpbGw9IiMxZjI5MzciPjx0c3BhbiB4PSIxNjIuNSIgeT0iOTcuMCI+UGVyc2lzdCByZW1vdGUgc3RhdGUgdmVyc2lvbiBOPC90c3Bhbj48L3RleHQ+PGxpbmUgeDE9IjI0NiIgeTE9IjE3NCIgeDI9IjgzIiB5Mj0iMTc0IiBzdHJva2U9IiM0NzU1NjkiIHN0cm9rZS13aWR0aD0iMS4zIiBzdHJva2UtZGFzaGFycmF5PSI2IDQiIG1hcmtlci1lbmQ9InVybCgjYXJyb3cpIi8+PHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgZm9udC1mYW1pbHk9IkFyaWFsLCBzYW5zLXNlcmlmIiBmb250LXNpemU9IjExIiBmb250LXdlaWdodD0ibm9ybWFsIiBmaWxsPSIjMWYyOTM3Ij48dHNwYW4geD0iMTYyLjUiIHk9IjE2MS4wIj5Db21taXQ8L3RzcGFuPjwvdGV4dD48bGluZSB4MT0iNzkiIHkxPSIyMzgiIHgyPSI0MTciIHkyPSIyMzgiIHN0cm9rZT0iIzQ3NTU2OSIgc3Ryb2tlLXdpZHRoPSIxLjMiIG1hcmtlci1lbmQ9InVybCgjYXJyb3cpIi8+PHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgZm9udC1mYW1pbHk9IkFyaWFsLCBzYW5zLXNlcmlmIiBmb250LXNpemU9IjExIiBmb250LXdlaWdodD0ibm9ybWFsIiBmaWxsPSIjMWYyOTM3Ij48dHNwYW4geD0iMjUwLjAiIHk9IjIxOC4wIj5SZW1vdGVTdGF0ZUNoYW5nZWRFdmVudChwcm9wZXJ0eSBJRHMsPC90c3Bhbj48dHNwYW4geD0iMjUwLjAiIGR5PSIxNCI+dmVyc2lvbiBOKTwvdHNwYW4+PC90ZXh0PjxsaW5lIHgxPSI0MjkiIHkxPSIzMDIiIHgyPSI1OTIiIHkyPSIzMDIiIHN0cm9rZT0iIzQ3NTU2OSIgc3Ryb2tlLXdpZHRoPSIxLjMiIG1hcmtlci1lbmQ9InVybCgjYXJyb3cpIi8+PHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgZm9udC1mYW1pbHk9IkFyaWFsLCBzYW5zLXNlcmlmIiBmb250LXNpemU9IjExIiBmb250LXdlaWdodD0ibm9ybWFsIiBmaWxsPSIjMWYyOTM3Ij48dHNwYW4geD0iNTEyLjUiIHk9IjI4OS4wIj5SZWxldmFuY2UgY2hlY2s8L3RzcGFuPjwvdGV4dD48cGF0aCBkPSJNIDYwMCAzNjYgaCA1NCB2IDI4IGggLTU0IiBmaWxsPSJub25lIiBzdHJva2U9IiM0NzU1NjkiIHN0cm9rZS13aWR0aD0iMS4zIiBtYXJrZXItZW5kPSJ1cmwoI2Fycm93KSIvPjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIGZvbnQtZmFtaWx5PSJBcmlhbCwgc2Fucy1zZXJpZiIgZm9udC1zaXplPSIxMSIgZm9udC13ZWlnaHQ9Im5vcm1hbCIgZmlsbD0iIzFmMjkzNyI+PHRzcGFuIHg9IjYyNy4wIiB5PSIzNTYuMCI+Q29hbGVzY2UgYnkgcmVtb3RlIElEPC90c3Bhbj48L3RleHQ+PGxpbmUgeDE9IjU5NiIgeTE9IjQzMCIgeDI9IjI1OCIgeTI9IjQzMCIgc3Ryb2tlPSIjNDc1NTY5IiBzdHJva2Utd2lkdGg9IjEuMyIgbWFya2VyLWVuZD0idXJsKCNhcnJvdykiLz48dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBmb250LWZhbWlseT0iQXJpYWwsIHNhbnMtc2VyaWYiIGZvbnQtc2l6ZT0iMTEiIGZvbnQtd2VpZ2h0PSJub3JtYWwiIGZpbGw9IiMxZjI5MzciPjx0c3BhbiB4PSI0MjUuMCIgeT0iNDE3LjAiPkxvYWQgbGF0ZXN0IHN0YXRlIGFuZCBtYXBwaW5nczwvdHNwYW4+PC90ZXh0PjxwYXRoIGQ9Ik0gNjAwIDQ5NCBoIDU0IHYgMjggaCAtNTQiIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzQ3NTU2OSIgc3Ryb2tlLXdpZHRoPSIxLjMiIG1hcmtlci1lbmQ9InVybCgjYXJyb3cpIi8+PHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgZm9udC1mYW1pbHk9IkFyaWFsLCBzYW5zLXNlcmlmIiBmb250LXNpemU9IjExIiBmb250LXdlaWdodD0ibm9ybWFsIiBmaWxsPSIjMWYyOTM3Ij48dHNwYW4geD0iNjI3LjAiIHk9IjQ4NC4wIj5SZXNvbHZlIGNhbm9uaWNhbCBzaWduYWxzPC90c3Bhbj48L3RleHQ+PHBhdGggZD0iTSA2MDAgNTU4IGggNTQgdiAyOCBoIC01NCIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjNDc1NTY5IiBzdHJva2Utd2lkdGg9IjEuMyIgbWFya2VyLWVuZD0idXJsKCNhcnJvdykiLz48dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBmb250LWZhbWlseT0iQXJpYWwsIHNhbnMtc2VyaWYiIGZvbnQtc2l6ZT0iMTEiIGZvbnQtd2VpZ2h0PSJub3JtYWwiIGZpbGw9IiMxZjI5MzciPjx0c3BhbiB4PSI2MjcuMCIgeT0iNTQ4LjAiPkV2YWx1YXRlIHR5cGVkIGhlYWx0aCBwcm9maWxlPC90c3Bhbj48L3RleHQ+PGxpbmUgeDE9IjU5NiIgeTE9IjYyMiIgeDI9IjI1OCIgeTI9IjYyMiIgc3Ryb2tlPSIjNDc1NTY5IiBzdHJva2Utd2lkdGg9IjEuMyIgbWFya2VyLWVuZD0idXJsKCNhcnJvdykiLz48dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBmb250LWZhbWlseT0iQXJpYWwsIHNhbnMtc2VyaWYiIGZvbnQtc2l6ZT0iMTEiIGZvbnQtd2VpZ2h0PSJub3JtYWwiIGZpbGw9IiMxZjI5MzciPjx0c3BhbiB4PSI0MjUuMCIgeT0iNjA5LjAiPlVwZGF0ZSBSZW1vdGUgY3VycmVudCBoZWFsdGg8L3RzcGFuPjwvdGV4dD48bGluZSB4MT0iNjA0IiB5MT0iNjg2IiB4Mj0iOTQyIiB5Mj0iNjg2IiBzdHJva2U9IiM0NzU1NjkiIHN0cm9rZS13aWR0aD0iMS4zIiBtYXJrZXItZW5kPSJ1cmwoI2Fycm93KSIvPjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIGZvbnQtZmFtaWx5PSJBcmlhbCwgc2Fucy1zZXJpZiIgZm9udC1zaXplPSIxMSIgZm9udC13ZWlnaHQ9Im5vcm1hbCIgZmlsbD0iIzFmMjkzNyI+PHRzcGFuIHg9Ijc3NS4wIiB5PSI2NzMuMCI+UGVyc2lzdCB0cmFuc2l0aW9uIHdoZW4gbWVhbmluZ2Z1bDwvdHNwYW4+PC90ZXh0PjxsaW5lIHgxPSI1OTYiIHkxPSI3NTAiIHgyPSI0MzMiIHkyPSI3NTAiIHN0cm9rZT0iIzQ3NTU2OSIgc3Ryb2tlLXdpZHRoPSIxLjMiIG1hcmtlci1lbmQ9InVybCgjYXJyb3cpIi8+PHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgZm9udC1mYW1pbHk9IkFyaWFsLCBzYW5zLXNlcmlmIiBmb250LXNpemU9IjExIiBmb250LXdlaWdodD0ibm9ybWFsIiBmaWxsPSIjMWYyOTM3Ij48dHNwYW4geD0iNTEyLjUiIHk9IjczNy4wIj5SZW1vdGVIZWFsdGhDaGFuZ2VkRXZlbnQ8L3RzcGFuPjwvdGV4dD48bGluZSB4MT0iNDI5IiB5MT0iODE0IiB4Mj0iNzY3IiB5Mj0iODE0IiBzdHJva2U9IiM0NzU1NjkiIHN0cm9rZS13aWR0aD0iMS4zIiBtYXJrZXItZW5kPSJ1cmwoI2Fycm93KSIvPjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIGZvbnQtZmFtaWx5PSJBcmlhbCwgc2Fucy1zZXJpZiIgZm9udC1zaXplPSIxMSIgZm9udC13ZWlnaHQ9Im5vcm1hbCIgZmlsbD0iIzFmMjkzNyI+PHRzcGFuIHg9IjYwMC4wIiB5PSI3OTQuMCI+RmluZCBhY3RpdmUgUmVtb3RlR3JvdXBUb1JlbW90ZTwvdHNwYW4+PHRzcGFuIHg9IjYwMC4wIiBkeT0iMTQiPm1lbWJlcnNoaXBzPC90c3Bhbj48L3RleHQ+PHBhdGggZD0iTSA3NzUgODc4IGggNTQgdiAyOCBoIC01NCIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjNDc1NTY5IiBzdHJva2Utd2lkdGg9IjEuMyIgbWFya2VyLWVuZD0idXJsKCNhcnJvdykiLz48dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBmb250LWZhbWlseT0iQXJpYWwsIHNhbnMtc2VyaWYiIGZvbnQtc2l6ZT0iMTEiIGZvbnQtd2VpZ2h0PSJub3JtYWwiIGZpbGw9IiMxZjI5MzciPjx0c3BhbiB4PSI4MDIuMCIgeT0iODY4LjAiPkNvYWxlc2NlIGJ5IGdyb3VwIElEPC90c3Bhbj48L3RleHQ+PGxpbmUgeDE9Ijc3MSIgeTE9Ijk0MiIgeDI9IjI1OCIgeTI9Ijk0MiIgc3Ryb2tlPSIjNDc1NTY5IiBzdHJva2Utd2lkdGg9IjEuMyIgbWFya2VyLWVuZD0idXJsKCNhcnJvdykiLz48dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBmb250LWZhbWlseT0iQXJpYWwsIHNhbnMtc2VyaWYiIGZvbnQtc2l6ZT0iMTEiIGZvbnQtd2VpZ2h0PSJub3JtYWwiIGZpbGw9IiMxZjI5MzciPjx0c3BhbiB4PSI1MTIuNSIgeT0iOTI5LjAiPkFwcGx5IG1lbWJlciBkZWx0YSB0byBhY2N1bXVsYXRvcjwvdHNwYW4+PC90ZXh0PjxwYXRoIGQ9Ik0gNzc1IDEwMDYgaCA1NCB2IDI4IGggLTU0IiBmaWxsPSJub25lIiBzdHJva2U9IiM0NzU1NjkiIHN0cm9rZS13aWR0aD0iMS4zIiBtYXJrZXItZW5kPSJ1cmwoI2Fycm93KSIvPjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIGZvbnQtZmFtaWx5PSJBcmlhbCwgc2Fucy1zZXJpZiIgZm9udC1zaXplPSIxMSIgZm9udC13ZWlnaHQ9Im5vcm1hbCIgZmlsbD0iIzFmMjkzNyI+PHRzcGFuIHg9IjgwMi4wIiB5PSI5OTYuMCI+RXZhbHVhdGUgRmxlZXRIZWFsdGhQb2xpY3k8L3RzcGFuPjwvdGV4dD48bGluZSB4MT0iNzcxIiB5MT0iMTA3MCIgeDI9IjI1OCIgeTI9IjEwNzAiIHN0cm9rZT0iIzQ3NTU2OSIgc3Ryb2tlLXdpZHRoPSIxLjMiIG1hcmtlci1lbmQ9InVybCgjYXJyb3cpIi8+PHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgZm9udC1mYW1pbHk9IkFyaWFsLCBzYW5zLXNlcmlmIiBmb250LXNpemU9IjExIiBmb250LXdlaWdodD0ibm9ybWFsIiBmaWxsPSIjMWYyOTM3Ij48dHNwYW4geD0iNTEyLjUiIHk9IjEwNTcuMCI+VXBkYXRlIFJlbW90ZUdyb3VwIGN1cnJlbnQgaGVhbHRoPC90c3Bhbj48L3RleHQ+PGxpbmUgeDE9Ijc3OSIgeTE9IjExMzQiIHgyPSI5NDIiIHkyPSIxMTM0IiBzdHJva2U9IiM0NzU1NjkiIHN0cm9rZS13aWR0aD0iMS4zIiBtYXJrZXItZW5kPSJ1cmwoI2Fycm93KSIvPjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIGZvbnQtZmFtaWx5PSJBcmlhbCwgc2Fucy1zZXJpZiIgZm9udC1zaXplPSIxMSIgZm9udC13ZWlnaHQ9Im5vcm1hbCIgZmlsbD0iIzFmMjkzNyI+PHRzcGFuIHg9Ijg2Mi41IiB5PSIxMTIxLjAiPlBlcnNpc3QgZ3JvdXAgdHJhbnNpdGlvbjwvdHNwYW4+PC90ZXh0PjxsaW5lIHgxPSI3NzEiIHkxPSIxMTk4IiB4Mj0iNDMzIiB5Mj0iMTE5OCIgc3Ryb2tlPSIjNDc1NTY5IiBzdHJva2Utd2lkdGg9IjEuMyIgbWFya2VyLWVuZD0idXJsKCNhcnJvdykiLz48dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBmb250LWZhbWlseT0iQXJpYWwsIHNhbnMtc2VyaWYiIGZvbnQtc2l6ZT0iMTEiIGZvbnQtd2VpZ2h0PSJub3JtYWwiIGZpbGw9IiMxZjI5MzciPjx0c3BhbiB4PSI2MDAuMCIgeT0iMTE4NS4wIj5SZW1vdGVHcm91cEhlYWx0aENoYW5nZWRFdmVudDwvdHNwYW4+PC90ZXh0PjxsaW5lIHgxPSI0MjkiIHkxPSIxMjYyIiB4Mj0iMTExNyIgeTI9IjEyNjIiIHN0cm9rZT0iIzQ3NTU2OSIgc3Ryb2tlLXdpZHRoPSIxLjMiIG1hcmtlci1lbmQ9InVybCgjYXJyb3cpIi8+PHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgZm9udC1mYW1pbHk9IkFyaWFsLCBzYW5zLXNlcmlmIiBmb250LXNpemU9IjExIiBmb250LXdlaWdodD0ibm9ybWFsIiBmaWxsPSIjMWYyOTM3Ij48dHNwYW4geD0iNzc1LjAiIHk9IjEyNDkuMCI+UmVzb2x2ZSBoZWFsdGggc3Vic2NyaXB0aW9uczwvdHNwYW4+PC90ZXh0PjxsaW5lIHgxPSIxMTIxIiB5MT0iMTMyNiIgeDI9IjI1OCIgeTI9IjEzMjYiIHN0cm9rZT0iIzQ3NTU2OSIgc3Ryb2tlLXdpZHRoPSIxLjMiIG1hcmtlci1lbmQ9InVybCgjYXJyb3cpIi8+PHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgZm9udC1mYW1pbHk9IkFyaWFsLCBzYW5zLXNlcmlmIiBmb250LXNpemU9IjExIiBmb250LXdlaWdodD0ibm9ybWFsIiBmaWxsPSIjMWYyOTM3Ij48dHNwYW4geD0iNjg3LjUiIHk9IjEzMTMuMCI+SW5zZXJ0IGRlbGl2ZXJ5L291dGJveCByZWNvcmRzPC90c3Bhbj48L3RleHQ+PHJlY3QgeD0iMS4wIiB5PSIxNDIwIiB3aWR0aD0iMTQ4IiBoZWlnaHQ9IjUyIiByeD0iOCIgZmlsbD0iI2Y4ZmFmYyIgc3Ryb2tlPSIjNGE1NTY4IiBzdHJva2Utd2lkdGg9IjEuMiIvPjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIGZvbnQtZmFtaWx5PSJBcmlhbCwgc2Fucy1zZXJpZiIgZm9udC1zaXplPSIxMiIgZm9udC13ZWlnaHQ9ImJvbGQiIGZpbGw9IiMxZjI5MzciPjx0c3BhbiB4PSI3NS4wIiB5PSIxNDQ3LjAiPlN0YXRlIGluZ2VzdGlvbjwvdHNwYW4+PC90ZXh0PjxyZWN0IHg9IjE3Ni4wIiB5PSIxNDIwIiB3aWR0aD0iMTQ4IiBoZWlnaHQ9IjUyIiByeD0iOCIgZmlsbD0iI2Y4ZmFmYyIgc3Ryb2tlPSIjNGE1NTY4IiBzdHJva2Utd2lkdGg9IjEuMiIvPjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIGZvbnQtZmFtaWx5PSJBcmlhbCwgc2Fucy1zZXJpZiIgZm9udC1zaXplPSIxMiIgZm9udC13ZWlnaHQ9ImJvbGQiIGZpbGw9IiMxZjI5MzciPjx0c3BhbiB4PSIyNTAuMCIgeT0iMTQ0Ny4wIj5EYXRhYmFzZTwvdHNwYW4+PC90ZXh0PjxyZWN0IHg9IjM1MS4wIiB5PSIxNDIwIiB3aWR0aD0iMTQ4IiBoZWlnaHQ9IjUyIiByeD0iOCIgZmlsbD0iI2Y4ZmFmYyIgc3Ryb2tlPSIjNGE1NTY4IiBzdHJva2Utd2lkdGg9IjEuMiIvPjx0ZXh0IHRleHQtYW5jaG9yPSJtaWRkbGUiIGZvbnQtZmFtaWx5PSJBcmlhbCwgc2Fucy1zZXJpZiIgZm9udC1zaXplPSIxMiIgZm9udC13ZWlnaHQ9ImJvbGQiIGZpbGw9IiMxZjI5MzciPjx0c3BhbiB4PSI0MjUuMCIgeT0iMTQ0Ny4wIj5TcHJpbmcgZXZlbnQgYnVzPC90c3Bhbj48L3RleHQ+PHJlY3QgeD0iNTI2LjAiIHk9IjE0MjAiIHdpZHRoPSIxNDgiIGhlaWdodD0iNTIiIHJ4PSI4IiBmaWxsPSIjZjhmYWZjIiBzdHJva2U9IiM0YTU1NjgiIHN0cm9rZS13aWR0aD0iMS4yIi8+PHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgZm9udC1mYW1pbHk9IkFyaWFsLCBzYW5zLXNlcmlmIiBmb250LXNpemU9IjEyIiBmb250LXdlaWdodD0iYm9sZCIgZmlsbD0iIzFmMjkzNyI+PHRzcGFuIHg9IjYwMC4wIiB5PSIxNDM5LjUiPlJlbW90ZSBoZWFsdGg8L3RzcGFuPjx0c3BhbiB4PSI2MDAuMCIgZHk9IjE1Ij5zZXJ2aWNlPC90c3Bhbj48L3RleHQ+PHJlY3QgeD0iNzAxLjAiIHk9IjE0MjAiIHdpZHRoPSIxNDgiIGhlaWdodD0iNTIiIHJ4PSI4IiBmaWxsPSIjZjhmYWZjIiBzdHJva2U9IiM0YTU1NjgiIHN0cm9rZS13aWR0aD0iMS4yIi8+PHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgZm9udC1mYW1pbHk9IkFyaWFsLCBzYW5zLXNlcmlmIiBmb250LXNpemU9IjEyIiBmb250LXdlaWdodD0iYm9sZCIgZmlsbD0iIzFmMjkzNyI+PHRzcGFuIHg9Ijc3NS4wIiB5PSIxNDQ3LjAiPkdyb3VwIGhlYWx0aCBzZXJ2aWNlPC90c3Bhbj48L3RleHQ+PHJlY3QgeD0iODc2LjAiIHk9IjE0MjAiIHdpZHRoPSIxNDgiIGhlaWdodD0iNTIiIHJ4PSI4IiBmaWxsPSIjZjhmYWZjIiBzdHJva2U9IiM0YTU1NjgiIHN0cm9rZS13aWR0aD0iMS4yIi8+PHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgZm9udC1mYW1pbHk9IkFyaWFsLCBzYW5zLXNlcmlmIiBmb250LXNpemU9IjEyIiBmb250LXdlaWdodD0iYm9sZCIgZmlsbD0iIzFmMjkzNyI+PHRzcGFuIHg9Ijk1MC4wIiB5PSIxNDQ3LjAiPkhpc3Rvcnkgc2VydmljZTwvdHNwYW4+PC90ZXh0PjxyZWN0IHg9IjEwNTEuMCIgeT0iMTQyMCIgd2lkdGg9IjE0OCIgaGVpZ2h0PSI1MiIgcng9IjgiIGZpbGw9IiNmOGZhZmMiIHN0cm9rZT0iIzRhNTU2OCIgc3Ryb2tlLXdpZHRoPSIxLjIiLz48dGV4dCB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBmb250LWZhbWlseT0iQXJpYWwsIHNhbnMtc2VyaWYiIGZvbnQtc2l6ZT0iMTIiIGZvbnQtd2VpZ2h0PSJib2xkIiBmaWxsPSIjMWYyOTM3Ij48dHNwYW4geD0iMTEyNS4wIiB5PSIxNDQ3LjAiPk5vdGlmaWNhdGlvbiBzZXJ2aWNlPC90c3Bhbj48L3RleHQ+PC9zdmc+" style="max-width: 100%; height: auto;" />
</p>

<details>
<summary>Mermaid source for diagram 7</summary>

```text
sequenceDiagram
    participant I as State ingestion
    participant DB as Database
    participant E as Spring event bus
    participant RH as Remote health service
    participant GH as Group health service
    participant H as History service
    participant N as Notification service

    I->>DB: Persist remote state version N
    DB-->>I: Commit
    I->>E: RemoteStateChangedEvent(property IDs, version N)

    E->>RH: Relevance check
    RH->>RH: Coalesce by remote ID
    RH->>DB: Load latest state and mappings
    RH->>RH: Resolve canonical signals
    RH->>RH: Evaluate typed health profile
    RH->>DB: Update Remote current health
    RH->>H: Persist transition when meaningful
    RH->>E: RemoteHealthChangedEvent

    E->>GH: Find active RemoteGroupToRemote memberships
    GH->>GH: Coalesce by group ID
    GH->>DB: Apply member delta to accumulator
    GH->>GH: Evaluate FleetHealthPolicy
    GH->>DB: Update RemoteGroup current health
    GH->>H: Persist group transition
    GH->>E: RemoteGroupHealthChangedEvent

    E->>N: Resolve health subscriptions
    N->>DB: Insert delivery/outbox records
```

</details>

---

## 13. Transaction boundaries

Recommended boundaries:

### State ingestion transaction

- Persist raw/latest state.
- Increment state version.
- Record changed property IDs.
- Commit.
- Publish `RemoteStateChangedEvent` after commit.

### Remote health transaction

- Lock current remote-health projection by remote ID.
- Ignore request when a newer health version already exists.
- Evaluate latest committed state.
- Update current projection.
- Insert history and outbox only on meaningful change.
- Commit.
- Publish `RemoteHealthChangedEvent`.

### Group health transaction

- Lock group accumulator/projection.
- Apply all pending member deltas.
- Evaluate the current policy.
- Update group projection.
- Insert history and outbox only on meaningful change.
- Commit.
- Publish `RemoteGroupHealthChangedEvent`.

Optimistic versioning should be used on current projections and accumulators.

---

## 14. Avoiding recursive group storms

If groups may contain other groups in the future, do not model this through `RemoteGroupToRemote`.

Use a separate entity:

```java
RemoteGroupToRemoteGroup
```

Then enforce:

- No cycles.
- Maximum hierarchy depth.
- Parent invalidation only after a child health transition.
- Coalescing at every parent.
- A unique evaluation version propagated upward.

For the first implementation, keeping `RemoteGroupToRemote` limited to `Remote` entities is safer.

---

## 15. APIs

### Evaluate one remote

```http
POST /plugins/RemoteHealth/evaluate
```

```json
{
  "remoteId": "remote-id",
  "force": false
}
```

### Get current remote health

```http
POST /plugins/RemoteHealth/getAll
```

```json
{
  "remoteIds": ["remote-id"],
  "minimumSeverityValue": 40,
  "humanInterventionRequired": true
}
```

### Evaluate one group

```http
POST /plugins/RemoteGroups/evaluateFleetHealth
```

```json
{
  "remoteGroupId": "group-id",
  "evaluateMembers": false,
  "forceFullReconciliation": false
}
```

### Get current group health

```http
POST /plugins/RemoteGroups/getAll
```

```json
{
  "minimumSeverityValue": 40,
  "healthCalculatedBefore": "2026-07-13T06:00:00Z"
}
```

### Query history

```http
POST /plugins/RemoteHealthHistory/getAll
POST /plugins/RemoteGroupHealthHistory/getAll
```

Filters should support:

- Subject IDs.
- Severity range.
- Matched rule IDs.
- Intervention flag.
- Valid time overlap.
- Device type.
- Group.
- Role.
- Policy.

### Manage subscriptions

```http
POST /plugins/HealthSubscriptions/getAll
POST /plugins/HealthSubscriptions/create
PUT  /plugins/HealthSubscriptions/update
DELETE /plugins/HealthSubscriptions/delete
```

---

## 16. Recommended implementation phases

### Phase 1 — individual health

- Canonical health signal entities.
- Schema-property mappings.
- `RemoteHealthProfile`, rules, and conditions.
- Current health projection on `Remote`.
- Device and gateway health evaluation.
- Remote health history.
- `RemoteHealthChangedEvent`.

### Phase 2 — explicit groups

- `RemoteGroup`.
- `RemoteGroupToRemote`.
- Roles, weights, required members, and active dates.
- `FleetHealthPolicy`, rules, and conditions.
- Current group projection.
- Full-scan group evaluation.
- Group health history.
- `RemoteGroupHealthChangedEvent`.

### Phase 3 — flood control

- Per-remote and per-group coalescing.
- Stable-duration and recovery hysteresis.
- Input/output hashes.
- Bounded partitioned executors.
- Dirty-group reconciliation.

### Phase 4 — incremental fleet aggregation

- Group accumulator.
- Severity buckets.
- Weighted severity deltas.
- Signal-support counters.
- Periodic full reconciliation.

### Phase 5 — notifications

- Persistent subscriptions.
- Health outbox.
- Webhook, WebSocket/SSE, email, and internal adapters.
- Retry and deduplication.
- Escalation-only and reminder policies.

---

## 17. Final recommendation

Use the following strict separation:

```text
DeviceType / Gateway integration
    defines how raw state becomes canonical health signals

RemoteHealthProfile
    defines how canonical signals become one Remote severity

RemoteGroupToRemote
    defines which Devices and Gateways belong to a population

FleetHealthPolicy
    defines how normalized member health becomes group/fleet health

Current projections
    provide fast operational reads

History entities
    preserve transitions and audit evidence

Spring events + transactional outbox
    notify interested parties without using the rules engine
```

Severity and aggregation must rely on stable entity references and canonical signal identifiers—not raw state-property display names.

This architecture allows two unrelated schemas, gateway infrastructure health, and mixed RemoteGroups to participate in one deterministic, typed, auditable health pipeline without flooding the backend.
