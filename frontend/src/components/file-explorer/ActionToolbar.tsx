import { Upload, FolderPlus } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { useRef } from 'react'

interface ActionToolbarProps {
  onNewFolder: () => void
  onUploadFile: (file: File) => void
  isUploading?: boolean
}

export function ActionToolbar({
  onNewFolder,
  onUploadFile,
  isUploading,
}: ActionToolbarProps) {
  const fileInputRef = useRef<HTMLInputElement>(null)

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (file) {
      onUploadFile(file)
      if (fileInputRef.current) fileInputRef.current.value = ''
    }
  }

  return (
    <div className="flex items-center gap-3">
      <input
        type="file"
        ref={fileInputRef}
        onChange={handleFileChange}
        className="hidden"
      />
      <Button
        onClick={onNewFolder}
        variant="outline"
        className="h-10 gap-2 rounded-xl border-[var(--line)] font-bold hover:bg-[var(--lagoon)]/5"
      >
        <FolderPlus size={18} />
        New Folder
      </Button>
      <Button
        onClick={() => fileInputRef.current?.click()}
        disabled={isUploading}
        className="h-10 gap-2 rounded-xl font-bold shadow-lg"
      >
        <Upload size={18} />
        {isUploading ? 'Uploading...' : 'Upload File'}
      </Button>
    </div>
  )
}
