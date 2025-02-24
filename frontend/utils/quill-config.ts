import Quill from 'quill';
import { Attributor } from 'parchment';

// Customize Quill's font size whitelist
const SizeStyle = Quill.import('attributors/style/size') as Attributor;
SizeStyle.whitelist = [
  '6px',
  '7px',
  '8px',
  '9px',
  '10px',
  '11px',
  '12px',
  '14px',
  '16px',
  '18px',
  '20px',
  '22px',
  '24px',
  '26px',
  '28px',
  '36px',
  '48px',
  '72px',
  '80px',
  '88px',
  '96px',
];
Quill.register(SizeStyle, true);

// Export Quill modules configuration
export const quillModules = {
  toolbar: false,
  clipboard: {
    matchVisual: false,
  },
};

// Export font sizes for the dropdown
export const fontSizes = SizeStyle.whitelist;
