# WebPptPortal

This project was generated using [Angular CLI](https://github.com/angular/angular-cli) version 19.1.7.

## Development server

To start a local development server, run:

```bash
ng serve
```

Once the server is running, open your browser and navigate to `http://localhost:4200/`. The application will automatically reload whenever you modify any of the source files.

## Code scaffolding

Angular CLI includes powerful code scaffolding tools. To generate a new component, run:

```bash
ng generate component component-name
```

For a complete list of available schematics (such as `components`, `directives`, or `pipes`), run:

```bash
ng generate --help
```

## Building

To build the project run:

```bash
ng build
```

This will compile your project and store the build artifacts in the `dist/` directory. By default, the production build optimizes your application for performance and speed.

## Running unit tests

To execute unit tests with the [Karma](https://karma-runner.github.io) test runner, use the following command:

```bash
ng test
```

## Running end-to-end tests

For end-to-end (e2e) testing, run:

```bash
ng e2e
```

Angular CLI does not come with an end-to-end testing framework by default. You can choose one that suits your needs.

## Additional Resources

For more information on using the Angular CLI, including detailed command references, visit the [Angular CLI Overview and Command Reference](https://angular.dev/tools/cli) page.

We've to create a content creator with certain features:

1. Ability to add standard PPT template/ layout slides, and update defined fields.
2. Ability to fix the font family for pre-defined text fields.
3. Ability to fix font size for pre-defined text fields.
4. Ability to highlight text using text highlight colour.
5. Ability to colour the text with font colour.
6. Ability to resize the font in case text is copied from external sources.
7. Features for alignment, bullets and numbering.
8. Enabling line spacing options for formating paragraphs.
9. Ability to insert shapes and edit text within shapes, color the shape, resize the shape, mark shape outline.
10. Insert a table in pre-defined grid format (2X2), (3X3); Ability to resize the table and edit content within the table.
11. Ability to upload image, resize image; Crop the image.

We'll start building the UI for this in angular v19 and for styling we'll prefer tailwind css.
