package xyz.gnarbot.gnar.commands.executors.settings

import net.dv8tion.jda.core.Permission
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.template.CommandTemplate
import xyz.gnarbot.gnar.commands.template.Executor
import xyz.gnarbot.gnar.utils.Context

@Command(
        id = 56,
        aliases = arrayOf("prefix"),
        usage = "(set|reset) [string]",
        description = "Set the bot prefix for the server.",
        category = Category.SETTINGS,
        permissions = arrayOf(Permission.MANAGE_SERVER)
)
class PrefixCommand : CommandTemplate() {
    private val mention = Regex("<@!?(\\d+)>|<#(\\d+)>|<@&(\\d+)>")

    @Executor(0, description = "Set the prefix.")
    fun set(context: Context, prefix: String) {
        if (prefix matches mention) {
            context.send().error("The prefix can't be set to a mention.").queue()
            return
        }

        if (context.data.command.prefix == prefix) {
            context.send().error("The prefix is already set to `$prefix`.").queue()
            return
        }

        context.data.command.prefix = prefix
        context.data.save()

        context.send().embed("Server Prefix") {
            desc {
                "The prefix has been set to `${context.data.command.prefix}`."
            }
        }.action().queue()
    }

    @Executor(1, description = "Reset to the default prefix.")
    fun reset(context: Context) {
        if (context.data.command.prefix == Bot.CONFIG.prefix) {
            context.send().error("The prefix is already set to the default.").queue()
            return
        }

        context.data.command.prefix = null
        context.data.save()

        context.send().embed("Server Prefix") {
            desc {
                "The prefix has been reset to `${context.data.command.prefix}`."
            }
        }.action().queue()
    }

    override fun helpMessage(context: Context, args: Array<String>) {
        helpMessage(context, args, null, buildString {
            append("Default prefix will still be valid.\n")
            val prefix = context.data.command.prefix
            append("Current prefix: `").append(prefix).append('`')
        })
    }
}