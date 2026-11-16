-- The application expects a private `images` bucket. The original Storage
-- policies target this bucket but did not create it, so uploads failed on a
-- fresh Supabase project with "Bucket not found".
INSERT INTO storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
VALUES ('images', 'images', false, 26214400, ARRAY['image/jpeg', 'image/png'])
ON CONFLICT (id) DO UPDATE SET
  name = EXCLUDED.name,
  public = false,
  file_size_limit = EXCLUDED.file_size_limit,
  allowed_mime_types = EXCLUDED.allowed_mime_types;

-- Recreate the owner-scoped policies idempotently so existing installations
-- have the same permissions as fresh installations.
DROP POLICY IF EXISTS "Owner uploads own images" ON storage.objects;
CREATE POLICY "Owner uploads own images" ON storage.objects
  FOR INSERT TO authenticated
  WITH CHECK (bucket_id = 'images' AND (storage.foldername(name))[1] = auth.uid()::text);

DROP POLICY IF EXISTS "Owner reads own images" ON storage.objects;
CREATE POLICY "Owner reads own images" ON storage.objects
  FOR SELECT TO authenticated
  USING (bucket_id = 'images' AND (storage.foldername(name))[1] = auth.uid()::text);

DROP POLICY IF EXISTS "Owner deletes own images" ON storage.objects;
CREATE POLICY "Owner deletes own images" ON storage.objects
  FOR DELETE TO authenticated
  USING (bucket_id = 'images' AND (storage.foldername(name))[1] = auth.uid()::text);
