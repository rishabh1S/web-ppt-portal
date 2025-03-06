## Web PPT Portal Backend

This is the backend service for the Web PPT Portal application, providing functionality to upload and edit PowerPoint presentations.

### Text Style Conversion

The backend now automatically converts PowerPoint text style properties to CSS style properties. This makes it easier to apply text styles directly in the frontend using Angular's `[ngStyle]` directive.

When retrieving slide elements, each element now includes a `cssStyle` field that contains CSS properties that can be directly applied to HTML elements, with a focus on text styling.

#### Supported Text Style Properties

The following PowerPoint text style properties are converted to CSS:

- **Font properties**:

  - `fontSize` → `font-size` in points
  - `fontColor` → `color`
  - `bold` → `font-weight: bold`
  - `italic` → `font-style: italic`
  - `underline` → `text-decoration: underline`
  - `strikethrough` → `text-decoration: line-through`
  - `highlightColor` → `background-color`

- **Alignment**:

  - `textAlign` → `text-align`
  - `horizontalCentered` → `text-align: center`
  - `verticalAlignment` → Flex-based vertical alignment

- **Spacing**:

  - `indent` → `text-indent`
  - `leftMargin` → `margin-left`
  - `rightMargin` → `margin-right`
  - `spacingBefore` → `margin-top`
  - `spacingAfter` → `margin-bottom`
  - Insets (top, right, bottom, left) → Corresponding padding values

- **Wrapping and overflow**:

  - `wordWrap` → `white-space` and `overflow-wrap`
  - `textAutofit` → Adaptive font sizing

- **Text direction**:
  - `textDirection` → `writing-mode` for vertical text

#### Example

Instead of processing complex PPT style properties in the frontend:

```typescript
// Complex PPT style properties (original)
const pptStyle = {
  fontColor: "#FF0000",
  fontSize: 24,
  bold: true,
  textAlign: "center",
  verticalAlignment: "middle",
  // ... many more properties
};

// Frontend would need to manually convert each property
```

You can now directly use the pre-converted CSS styles:

```typescript
// In your Angular component template
<div [ngStyle]="slideElement.cssStyle">
  {{ slideElement.content.text }}
</div>
```

This feature reduces frontend code complexity and ensures consistent text styling across all slide elements.

### API Endpoints
