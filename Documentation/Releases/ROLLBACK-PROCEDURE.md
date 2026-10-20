# Rollback procedure

What to do when a published Qeetrix version turns out to be broken. Maven repositories treat a published version as
immutable, and apps resolve exact versions, so a release is never edited or deleted to "fix" it: it is superseded.

## When a release is bad

1. **Stop new adoption.** Announce the affected version and the last good one where Qeet Group's app teams will see
   it, and mark the release's notes "Do not use — see x.y.z".
2. **Apps go back to the last good version**: in the app, change `com.qeetgroup:qeetrix:<bad>` to
   `com.qeetgroup:qeetrix:<good>` and rebuild. Nothing else in the app changes unless it adopted API that only the
   bad version has.
3. **Fix forward.** Publish a patch release (`x.y.Z+1`) from a commit that fixes the defect, through the normal
   release-candidate steps and approval ([RELEASE-CHECKLIST.md](RELEASE-CHECKLIST.md)). Apps move to it.
4. **Record it** in the CHANGELOG under the patch release, and in the release notes of the bad version.

## Deleting a version

Only for a version that must not be downloaded at all — a leaked secret, or material Qeet Group has no right to
distribute — and only by the owner, in the registry's own interface. Deleting breaks every build that pinned that
version, so the announcement in step 1 says so. Maven Central does not allow deletion; that is one reason it is not
the first registry ([0018](../Decisions/0018-maven-distribution.md)).

## What makes rollback cheap

- Versions are immutable and pinned exactly by apps, so going back is one line.
- Each release candidate keeps its evidence — provenance, checksums, the consumer's results
  (`build/release-candidate/evidence`, or the release-candidate workflow's artifact) — so the last good version's
  verification can be found and compared.
- Behaviour changes are listed in the CHANGELOG, so an app knows what it gives up by going back.
