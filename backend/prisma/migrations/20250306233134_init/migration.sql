-- CreateTable
CREATE TABLE "Presentation" (
    "id" TEXT NOT NULL,
    "name" TEXT NOT NULL,
    "height" INTEGER NOT NULL DEFAULT 600,
    "width" INTEGER NOT NULL DEFAULT 800,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "Presentation_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "slides" (
    "id" TEXT NOT NULL,
    "presentation_id" TEXT NOT NULL,
    "slide_number" INTEGER NOT NULL,
    "html_content" TEXT NOT NULL,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "slides_pkey" PRIMARY KEY ("id")
);

-- AddForeignKey
ALTER TABLE "slides" ADD CONSTRAINT "slides_presentation_id_fkey" FOREIGN KEY ("presentation_id") REFERENCES "Presentation"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
