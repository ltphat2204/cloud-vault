import { useState } from 'react'
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { useShares } from '@/hooks/queries/use-shares'
import type { ResourceType, Permission } from '@/types'
import { UserPlus, Link, Trash2, Globe, Shield, Clock, Copy, Check } from 'lucide-react'
import { toast } from 'sonner'

interface ShareDialogProps {
  isOpen: boolean
  onClose: () => void
  resourceId: string
  resourceType: ResourceType
  resourceName: string
}

export function ShareDialog({
  isOpen,
  onClose,
  resourceId,
  resourceType,
  resourceName,
}: ShareDialogProps) {
  const {
    shares,
    shareInternal,
    createPublicLink,
    revokeShare,
    updatePermission,
    isSharing,
  } = useShares(resourceType, resourceId)

  const [email, setEmail] = useState('')
  const [permission, setPermission] = useState<Permission>('VIEW')
  const [copied, setCopied] = useState(false)

  const handleShareInternal = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!email.trim()) return
    await shareInternal({
      resourceId,
      resourceType,
      userEmail: email.trim(),
      permission,
    })
    setEmail('')
  }

  const handleCreatePublicLink = async () => {
    await createPublicLink({
      resourceId,
      resourceType,
    })
  }

  const copyToClipboard = (text: string) => {
    navigator.clipboard.writeText(text)
    setCopied(true)
    toast.success('Link copied to clipboard')
    setTimeout(() => setCopied(false), 2000)
  }

  const internalShares = shares.filter((s) => s.sharedWithUser)
  const publicLinks = shares.filter((s) => s.accessToken)

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="rounded-[2rem] sm:max-w-[500px] overflow-hidden p-0 border-0 shadow-2xl">
        <div className="bg-[var(--lagoon-deep)] p-8 text-white">
          <DialogHeader>
            <DialogTitle className="text-3xl font-bold flex items-center gap-3">
              <Globe className="opacity-80" />
              Share Resource
            </DialogTitle>
            <p className="text-white/70 font-medium mt-2">
              Managing access for <span className="text-white font-bold">{resourceName}</span>
            </p>
          </DialogHeader>
        </div>

        <Tabs defaultValue="internal" className="w-full">
          <div className="px-8 pt-4">
            <TabsList className="grid w-full grid-cols-2 rounded-2xl bg-[var(--lagoon)]/10 p-1">
              <TabsTrigger
                value="internal"
                className="rounded-xl data-[state=active]:bg-white data-[state=active]:shadow-sm"
              >
                <UserPlus size={16} className="mr-2" />
                Internal
              </TabsTrigger>
              <TabsTrigger
                value="public"
                className="rounded-xl data-[state=active]:bg-white data-[state=active]:shadow-sm"
              >
                <Link size={16} className="mr-2" />
                Public Link
              </TabsTrigger>
            </TabsList>
          </div>

          <div className="p-8 pt-6 overflow-y-auto max-h-[60vh]">
            <TabsContent value="internal" className="m-0 space-y-6">
              <form onSubmit={handleShareInternal} className="space-y-4">
                <div className="grid gap-4 sm:grid-cols-[1fr_auto_auto]">
                  <div className="space-y-2">
                    <Label className="text-[10px] font-bold text-[var(--sea-ink-soft)] uppercase tracking-wider">
                      User Email
                    </Label>
                    <Input
                      placeholder="user@example.com"
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                      className="rounded-xl border-[var(--line)]"
                    />
                  </div>
                  <div className="space-y-2">
                    <Label className="text-[10px] font-bold text-[var(--sea-ink-soft)] uppercase tracking-wider">
                      Role
                    </Label>
                    <Select
                      value={permission}
                      onValueChange={(v) => setPermission(v as Permission)}
                    >
                      <SelectTrigger className="w-28 rounded-xl border-[var(--line)]">
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent className="rounded-xl">
                        <SelectItem value="VIEW">Viewer</SelectItem>
                        <SelectItem value="EDIT">Editor</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                  <div className="flex items-end">
                    <Button
                      type="submit"
                      disabled={!email.trim() || isSharing}
                      className="rounded-xl font-bold"
                    >
                      Share
                    </Button>
                  </div>
                </div>
              </form>

              <div className="space-y-4">
                <h4 className="text-sm font-bold text-[var(--sea-ink)] flex items-center gap-2">
                  <Shield size={16} className="text-[var(--lagoon-deep)]" />
                  Shared with users
                </h4>
                {internalShares.length === 0 ? (
                  <p className="text-sm text-[var(--sea-ink-soft)] italic py-4 text-center bg-gray-50 rounded-2xl">
                    Not shared with anyone yet
                  </p>
                ) : (
                  <div className="space-y-3">
                    {internalShares.map((share) => (
                      <div
                        key={share.id}
                        className="flex items-center justify-between p-3 rounded-2xl bg-white border border-[var(--line)] hover:shadow-sm transition-shadow"
                      >
                        <div className="overflow-hidden mr-4">
                          <p className="text-sm font-bold text-[var(--sea-ink)] truncate">
                            {share.sharedWithUser?.email}
                          </p>
                          <p className="text-[10px] text-[var(--sea-ink-soft)] uppercase font-bold tracking-tight">
                            {share.permission === 'VIEW' ? 'Viewer' : 'Editor'}
                          </p>
                        </div>
                        <div className="flex items-center gap-2">
                          <Select
                            value={share.permission}
                            onValueChange={(v) =>
                              updatePermission({ id: share.id, permission: v as Permission })
                            }
                          >
                            <SelectTrigger className="h-8 w-24 text-xs rounded-lg border-0 bg-[var(--lagoon)]/5 hover:bg-[var(--lagoon)]/10">
                              <SelectValue />
                            </SelectTrigger>
                            <SelectContent className="rounded-xl">
                              <SelectItem value="VIEW">Viewer</SelectItem>
                              <SelectItem value="EDIT">Editor</SelectItem>
                            </SelectContent>
                          </Select>
                          <Button
                            variant="ghost"
                            size="icon"
                            onClick={() => revokeShare(share.id)}
                            className="h-8 w-8 text-red-500 hover:text-red-600 hover:bg-red-50 rounded-lg"
                          >
                            <Trash2 size={14} />
                          </Button>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </TabsContent>

            <TabsContent value="public" className="m-0 space-y-6">
              <div className="space-y-4">
                <div className="bg-[var(--lagoon)]/5 p-6 rounded-[1.5rem] border border-[var(--lagoon)]/10">
                  <div className="flex items-center gap-4 mb-4">
                    <div className="h-10 w-10 rounded-xl bg-[var(--lagoon-deep)] flex items-center justify-center text-white">
                      <Globe size={20} />
                    </div>
                    <div>
                      <h4 className="font-bold text-[var(--sea-ink)]">Public Link Sharing</h4>
                      <p className="text-xs text-[var(--sea-ink-soft)]">
                        Anyone with the link can view this resource
                      </p>
                    </div>
                  </div>
                  
                  {publicLinks.length === 0 ? (
                    <Button
                      onClick={handleCreatePublicLink}
                      disabled={isSharing}
                      className="w-full rounded-xl font-bold py-6 bg-[var(--lagoon-deep)] hover:bg-[var(--lagoon-deep)]/90"
                    >
                      <Link size={18} className="mr-2" />
                      Create Public Link
                    </Button>
                  ) : (
                    <div className="space-y-4">
                      {publicLinks.map((link) => (
                        <div key={link.id} className="space-y-3">
                          <div className="flex items-center gap-2">
                            <div className="flex-1 bg-white border border-[var(--line)] rounded-xl p-3 flex items-center justify-between overflow-hidden">
                              <span className="text-xs text-[var(--sea-ink-soft)] truncate mr-2">
                                {link.publicUrl}
                              </span>
                              <Button
                                size="sm"
                                variant="ghost"
                                onClick={() => copyToClipboard(link.publicUrl!)}
                                className="h-7 px-2 rounded-lg hover:bg-[var(--lagoon)]/10"
                              >
                                {copied ? <Check size={14} /> : <Copy size={14} />}
                              </Button>
                            </div>
                            <Button
                              variant="ghost"
                              size="icon"
                              onClick={() => revokeShare(link.id)}
                              className="h-10 w-10 text-red-500 hover:text-red-600 hover:bg-red-50 rounded-xl"
                            >
                              <Trash2 size={16} />
                            </Button>
                          </div>
                          {link.expiresAt && (
                            <div className="flex items-center gap-2 text-[10px] font-bold text-[var(--sea-ink-soft)] uppercase tracking-wider px-1">
                              <Clock size={12} />
                              Expires: {new Date(link.expiresAt).toLocaleDateString()}
                            </div>
                          )}
                        </div>
                      ))}
                    </div>
                  )}
                </div>

                <div className="bg-gray-50 p-4 rounded-xl flex items-start gap-3">
                  <Shield size={16} className="text-[var(--sea-ink-soft)] mt-0.5" />
                  <p className="text-xs text-[var(--sea-ink-soft)] leading-relaxed">
                    Public links allow viewing only. For security, these links do not allow editing
                    unless specifically authorized via internal sharing.
                  </p>
                </div>
              </div>
            </TabsContent>
          </div>
        </Tabs>
      </DialogContent>
    </Dialog>
  )
}
