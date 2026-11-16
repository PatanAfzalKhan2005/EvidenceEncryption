-- Match the owner-scoped read/insert/delete access already used by ImageSeal,
-- while allowing authenticated owners to update their own records.
GRANT UPDATE ON public.images TO authenticated;
DROP POLICY IF EXISTS "Owner can update own images" ON public.images;
CREATE POLICY "Owner can update own images" ON public.images
  FOR UPDATE TO authenticated
  USING (owner = auth.uid())
  WITH CHECK (owner = auth.uid());

GRANT UPDATE ON public.verifications TO authenticated;
DROP POLICY IF EXISTS "Owner can update own verifications" ON public.verifications;
CREATE POLICY "Owner can update own verifications" ON public.verifications
  FOR UPDATE TO authenticated
  USING (owner = auth.uid())
  WITH CHECK (
    owner = auth.uid() AND EXISTS (
      SELECT 1 FROM public.images
      WHERE id = original_image_id AND owner = auth.uid()
    )
  );

GRANT UPDATE ON public.image_versions TO authenticated;
DROP POLICY IF EXISTS "Owner can update own image versions" ON public.image_versions;
CREATE POLICY "Owner can update own image versions" ON public.image_versions
  FOR UPDATE TO authenticated
  USING (owner = auth.uid())
  WITH CHECK (
    owner = auth.uid() AND EXISTS (
      SELECT 1 FROM public.images
      WHERE id = original_image_id AND owner = auth.uid()
    )
  );

GRANT UPDATE ON public.duplicate_uploads TO authenticated;
DROP POLICY IF EXISTS "Owner can update own duplicate uploads" ON public.duplicate_uploads;
CREATE POLICY "Owner can update own duplicate uploads" ON public.duplicate_uploads
  FOR UPDATE TO authenticated
  USING (owner = auth.uid())
  WITH CHECK (
    owner = auth.uid() AND EXISTS (
      SELECT 1 FROM public.images
      WHERE id = existing_image_id AND owner = auth.uid()
    )
  );

NOTIFY pgrst, 'reload schema';
